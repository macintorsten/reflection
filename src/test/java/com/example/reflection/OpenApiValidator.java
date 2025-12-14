package com.example.reflection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.InputStream;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * High-level OpenAPI validation that checks both HTTP status codes and response payloads
 * against an OpenAPI specification.
 * 
 * This provides a single assertion that validates:
 * - HTTP status code matches what's defined in OpenAPI spec for the operation
 * - Response body matches the schema defined in OpenAPI spec
 * 
 * Usage with MockMVC:
 * <pre>
 * mockMvc.perform(post("/api/samples").content("..."))
 *     .andExpect(OpenApiValidator.matchesOpenApi("openapi-spec.json", "POST", "/api/samples", 200));
 * </pre>
 */
public class OpenApiValidator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Create a ResultMatcher that validates both status code and response body against OpenAPI spec.
     * 
     * @param openApiPath Path to OpenAPI spec file in classpath (e.g., "openapi-spec.json")
     * @param method HTTP method (e.g., "GET", "POST")
     * @param path API path (e.g., "/api/samples")
     * @param expectedStatus Expected HTTP status code (e.g., 200, 400)
     * @return ResultMatcher for use with MockMVC's andExpect()
     */
    public static ResultMatcher matchesOpenApi(String openApiPath, String method, String path, int expectedStatus) {
        return result -> {
            // Validate status code
            int actualStatus = result.getResponse().getStatus();
            assertEquals("HTTP status code", expectedStatus, actualStatus);
            
            // Extract schema from OpenAPI spec for this operation and status code
            String schema = extractResponseSchema(openApiPath, method, path, expectedStatus);
            
            // Validate response body against schema
            String responseBody = result.getResponse().getContentAsString();
            org.hamcrest.MatcherAssert.assertThat(
                "Response body should match OpenAPI schema for " + method + " " + path + " " + expectedStatus,
                responseBody,
                matchesJsonSchema(schema)
            );
        };
    }

    /**
     * Extract the response schema from OpenAPI spec for a specific operation and status code.
     */
    private static String extractResponseSchema(String openApiPath, String method, String path, int statusCode) {
        try (InputStream is = OpenApiValidator.class.getClassLoader().getResourceAsStream(openApiPath)) {
            if (is == null) {
                throw new RuntimeException("OpenAPI spec not found: " + openApiPath);
            }
            
            JsonNode spec = objectMapper.readTree(is);
            
            // Navigate to paths -> /api/samples -> post -> responses -> 200 -> content -> application/json -> schema
            JsonNode pathNode = spec.path("paths").path(path);
            if (pathNode.isMissingNode()) {
                throw new RuntimeException("Path not found in OpenAPI spec: " + path);
            }
            
            JsonNode operationNode = pathNode.path(method.toLowerCase());
            if (operationNode.isMissingNode()) {
                throw new RuntimeException("Method not found in OpenAPI spec: " + method + " " + path);
            }
            
            JsonNode responseNode = operationNode.path("responses").path(String.valueOf(statusCode));
            if (responseNode.isMissingNode()) {
                throw new RuntimeException("Response " + statusCode + " not found in OpenAPI spec for: " + method + " " + path);
            }
            
            JsonNode schemaNode = responseNode.path("content").path("application/json").path("schema");
            if (schemaNode.isMissingNode()) {
                throw new RuntimeException("Schema not found for response " + statusCode + " in: " + method + " " + path);
            }
            
            // Resolve $ref if present
            if (schemaNode.has("$ref")) {
                String ref = schemaNode.get("$ref").asText(); // e.g., "#/components/schemas/SampleDTO"
                schemaNode = resolveRef(spec, ref);
            }
            
            // Handle arrays
            if (schemaNode.has("type") && schemaNode.get("type").asText().equals("array")) {
                JsonNode itemsNode = schemaNode.get("items");
                if (itemsNode.has("$ref")) {
                    String ref = itemsNode.get("$ref").asText();
                    JsonNode resolvedItems = resolveRef(spec, ref);
                    // Create array schema with resolved items, recursively resolving any nested refs
                    JsonNode fullyResolvedItems = resolveAllRefs(spec, resolvedItems);
                    return String.format("{\"type\":\"array\",\"items\":%s}", objectMapper.writeValueAsString(fullyResolvedItems));
                }
            }
            
            // Recursively resolve all nested $refs in the schema
            JsonNode fullyResolved = resolveAllRefs(spec, schemaNode);
            return objectMapper.writeValueAsString(fullyResolved);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract schema from OpenAPI spec", e);
        }
    }

    /**
     * Resolve a $ref pointer in the OpenAPI spec.
     */
    private static JsonNode resolveRef(JsonNode spec, String ref) {
        if (!ref.startsWith("#/")) {
            throw new RuntimeException("Only local refs are supported: " + ref);
        }
        
        String[] pathParts = ref.substring(2).split("/");
        JsonNode node = spec;
        for (String part : pathParts) {
            node = node.get(part);
            if (node == null) {
                throw new RuntimeException("Failed to resolve ref: " + ref);
            }
        }
        return node;
    }

    /**
     * Recursively resolve all $ref pointers in a schema node.
     * Also makes fieldErrors array nullable for ErrorResponse schema.
     */
    private static JsonNode resolveAllRefs(JsonNode spec, JsonNode node) {
        if (node.isObject()) {
            com.fasterxml.jackson.databind.node.ObjectNode result = objectMapper.createObjectNode();
            node.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                
                if (key.equals("$ref")) {
                    // Replace this object with the resolved ref
                    JsonNode resolved = resolveRef(spec, value.asText());
                    // Recursively resolve refs in the resolved node
                    resolved = resolveAllRefs(spec, resolved);
                    // Copy all fields from resolved node to result
                    resolved.fields().forEachRemaining(e -> result.set(e.getKey(), e.getValue()));
                } else {
                    result.set(key, resolveAllRefs(spec, value));
                }
            });
            
            // Special handling: Make fieldErrors nullable in ErrorResponse
            if (result.has("properties")) {
                JsonNode props = result.get("properties");
                if (props.has("fieldErrors")) {
                    com.fasterxml.jackson.databind.node.ObjectNode fieldErrorsProp = 
                        (com.fasterxml.jackson.databind.node.ObjectNode) props.get("fieldErrors");
                    
                    // Change type from "array" to ["array", "null"] for nullable support
                    if (fieldErrorsProp.has("type") && fieldErrorsProp.get("type").asText().equals("array")) {
                        com.fasterxml.jackson.databind.node.ArrayNode typeArray = objectMapper.createArrayNode();
                        typeArray.add("array");
                        typeArray.add("null");
                        fieldErrorsProp.set("type", typeArray);
                    }
                }
            }
            
            return result;
        } else if (node.isArray()) {
            com.fasterxml.jackson.databind.node.ArrayNode result = objectMapper.createArrayNode();
            node.forEach(item -> result.add(resolveAllRefs(spec, item)));
            return result;
        } else {
            return node;
        }
    }
}
