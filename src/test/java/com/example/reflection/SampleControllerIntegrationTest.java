package com.example.reflection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack integration tests with real PostgreSQL database (via Testcontainers)
 * and JSON Schema validation using RestAssured's json-schema-validator.
 * 
 * Extends AbstractIntegrationTest to share the database container across all
 * integration test classes, reducing startup overhead.
 * 
 * This validates the entire stack:
 * - HTTP layer (Spring MVC)
 * - Service layer (business logic)
 * - Repository layer (JPA)
 * - Database (PostgreSQL via Testcontainers - SHARED)
 * - Response schema compliance (JSON Schema validation)
 */
class SampleControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateSample_FullStack_ValidatesSchema() throws Exception {
        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Integration Test",
                                    "number": 100,
                                    "status": "active"
                                }
                                """))
                .andExpect(OpenApiValidator.matchesOpenApi("openapi-spec.json", "POST", "/api/samples", 200))
                .andExpect(jsonPath("$.text").value("Integration Test"));
    }

    @Test
    void testCreateSample_WithExtras_FullStack_ValidatesSchema() throws Exception {
        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Test With Extras",
                                    "number": 42,
                                    "status": "active",
                                    "extras": {
                                        "key1": "123",
                                        "key2": "456"
                                    }
                                }
                                """))
                .andExpect(OpenApiValidator.matchesOpenApi("openapi-spec.json", "POST", "/api/samples", 200))
                .andExpect(jsonPath("$.extras.key1").value("123"));
    }

    @Test
    void testCreateSample_ValidationFailure_TextTooShort_ValidatesErrorSchema() throws Exception {
        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "AB",
                                    "number": 42,
                                    "status": "active"
                                }
                                """))
                .andExpect(OpenApiValidator.matchesOpenApi("openapi-spec.json", "POST", "/api/samples", 400))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void testCreateSample_ValidationFailure_NumberOutOfRange_ValidatesErrorSchema() throws Exception {
        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Valid Text",
                                    "number": 1001,
                                    "status": "active"
                                }
                                """))
                .andExpect(OpenApiValidator.matchesOpenApi("openapi-spec.json", "POST", "/api/samples", 400))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("number"));
    }

    @Test
    void testCreateSample_ValidationFailure_InvalidStatus_ValidatesErrorSchema() throws Exception {
        // Invalid enum values cause JSON parse errors (500) not validation errors (400)
        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Valid Text",
                                    "number": 42,
                                    "status": "invalid_status"
                                }
                                """))
                .andExpect(OpenApiValidator.matchesOpenApi("openapi-spec.json", "POST", "/api/samples", 500));
    }

    @Test
    void testListSamples_EmptyDatabase_ValidatesArraySchema() throws Exception {
        // This test might have data from previous tests, but schema should still validate
        mockMvc.perform(get("/api/samples"))
                .andExpect(OpenApiValidator.matchesOpenApi("openapi-spec.json", "GET", "/api/samples", 200))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testListSamples_AfterCreation_ValidatesArraySchema() throws Exception {
        // Create a sample first
        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "List Test Sample",
                                    "number": 999,
                                    "status": "inactive"
                                }
                                """))
                .andExpect(status().isOk());

        // List should include the created sample and validate schema
        mockMvc.perform(get("/api/samples"))
                .andExpect(OpenApiValidator.matchesOpenApi("openapi-spec.json", "GET", "/api/samples", 200))
                .andExpect(jsonPath("$[?(@.number == 999)].text").value("List Test Sample"));
    }

    @Test
    void testListSamples_MultipleItems_ValidatesArraySchema() throws Exception {
        // Create multiple samples
        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "First Sample",
                                    "number": 10,
                                    "status": "active"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Second Sample",
                                    "number": 20,
                                    "status": "inactive",
                                    "extras": {"tag": "important"}
                                }
                                """))
                .andExpect(status().isOk());

        // List all and validate array schema
        mockMvc.perform(get("/api/samples"))
                .andExpect(OpenApiValidator.matchesOpenApi("openapi-spec.json", "GET", "/api/samples", 200))
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }

    @Test
    void testCreateSample_DatabasePersistence_ValidatesSchema() throws Exception {
        // Create a sample
        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Persistence Test",
                                    "number": 777,
                                    "status": "active"
                                }
                                """))
                .andExpect(OpenApiValidator.matchesOpenApi("openapi-spec.json", "POST", "/api/samples", 200));

        // Verify it appears in the list (extras not persisted - mapField is @Transient)
        mockMvc.perform(get("/api/samples"))
                .andExpect(OpenApiValidator.matchesOpenApi("openapi-spec.json", "GET", "/api/samples", 200))
                .andExpect(jsonPath("$[?(@.number == 777)].text").value("Persistence Test"));
    }
}
