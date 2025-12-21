package com.example.reflection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Validates that the checked-in OpenAPI spec matches the currently running application.
 * 
 * This test ensures:
 * 1. The API implementation matches the checked-in spec
 * 2. When API changes, the test fails to alert developers to update the spec or bump API version
 * 3. Prevents accidental API breaking changes from being deployed
 * 
 * Developers should either:
 * - Update spec: .github/scripts/update-openapi-spec.sh
 * - Create new version: src/main/java/.../web/controller/v2/...
 */
class ApiSpecValidationTest extends AbstractIntegrationTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testApiSpecIsUpToDate() throws Exception {
        // Fetch the current OpenAPI spec from the running application using MockMvc
        String currentSpec = mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        assertThat(currentSpec).isNotEmpty();
        
        // Load the checked-in baseline spec
        String baselineSpec = loadBaselineSpec();
        assertThat(baselineSpec).isNotEmpty();
        
        // Parse both specs as JSON objects for comparison
        Object currentJson = normalizeSpec(mapper.readValue(currentSpec, Object.class));
        Object baselineJson = normalizeSpec(mapper.readValue(baselineSpec, Object.class));
        
        // Compare the specs
        assertThat(currentJson)
            .as("""
                API spec has changed! This typically means:
                
                1. You changed an existing endpoint, request/response structure, or validation rules.
                
                2. You have two options:
                   a) If the change is NON-BREAKING (adding optional fields, new endpoints):
                      Update the baseline spec: .github/scripts/update-openapi-spec.sh
                      
                   b) If the change is BREAKING (removing fields, changing types, renaming endpoints):
                      Create a new API version: src/main/java/.../web/controller/v2/...
                      Then update specs for both v1 and v2.
                
                3. Current spec from running app vs baseline spec in docs/openapi.json differ.
                
                To debug, regenerate spec locally and review changes:
                  git diff docs/openapi.json
                """)
            .isEqualTo(baselineJson);
    }

    private static Object normalizeSpec(Object openApiJson) {
        if (openApiJson instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> root = (Map<String, Object>) map;
            // Server URLs are environment-dependent (localhost vs localhost:8080) and not part of the API contract.
            root.remove("servers");
        }
        return openApiJson;
    }

    /**
     * Load the baseline OpenAPI spec from the classpath.
     * The spec is located at docs/openapi.json but copied to classpath during build.
     */
    private String loadBaselineSpec() throws IOException {
        // Maven will make this available in the classpath during the build
        try (InputStream is = ApiSpecValidationTest.class.getClassLoader()
                .getResourceAsStream("openapi.json")) {
            
            if (is == null) {
                throw new RuntimeException("""
                    Baseline OpenAPI spec not found in classpath: openapi.json
                    
                    This file should be copied from docs/openapi.json
                    during the Maven build process. Check your pom.xml resources configuration.
                    """);
            }
            
            return new String(is.readAllBytes());
        }
    }
}
