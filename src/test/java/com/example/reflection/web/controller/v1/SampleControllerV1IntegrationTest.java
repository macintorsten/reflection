package com.example.reflection.web.controller.v1;

import com.example.reflection.AbstractIntegrationTest;
import com.example.reflection.OpenApiValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack integration tests for V1 API with real PostgreSQL database (via Testcontainers).
 * Tests the entire stack: HTTP → Controller → Service → Repository → Database
 */
class SampleControllerV1IntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateSample_FullStack_ValidatesSchema() throws Exception {
        mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Integration Test",
                                    "number": 100,
                                    "status": "active"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").value("Integration Test"))
                .andExpect(jsonPath("$.number").value(100))
                .andExpect(jsonPath("$.status").value("active"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testCreateSample_WithExtras_FullStack() throws Exception {
        mockMvc.perform(post("/api/v1/samples")
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.extras.key1").value("123"))
                .andExpect(jsonPath("$.extras.key2").value("456"));
    }

    @Test
    void testCreateSample_ValidationFailure_TextTooShort() throws Exception {
        mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "AB",
                                    "number": 42,
                                    "status": "active"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void testCreateSample_ValidationFailure_NumberOutOfRange() throws Exception {
        mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Valid Text",
                                    "number": 1001,
                                    "status": "active"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("number"));
    }

    @Test
    void testCreateSample_ValidationFailure_InvalidStatus() throws Exception {
        // Invalid enum values now return 400 Bad Request (handled by JSON parser exception handler)
        mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Valid Text",
                                    "number": 42,
                                    "status": "invalid_status"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testListSamples_EmptyDatabase() throws Exception {
        // This test might have data from previous tests, but should still return array
        mockMvc.perform(get("/api/v1/samples"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testListSamples_AfterCreation() throws Exception {
        // Create a sample first
        mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "List Test Sample",
                                    "number": 999,
                                    "status": "inactive"
                                }
                                """))
                .andExpect(status().isCreated());

        // List should include the created sample
        mockMvc.perform(get("/api/v1/samples"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.number == 999)].text").value("List Test Sample"));
    }

    @Test
    void testListSamples_MultipleItems() throws Exception {
        // Create multiple samples
        mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "First Sample",
                                    "number": 10,
                                    "status": "active"
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Second Sample",
                                    "number": 20,
                                    "status": "inactive",
                                    "extras": {"tag": "1"}
                                }
                                """))
                .andExpect(status().isCreated());

        // List all and validate array
        mockMvc.perform(get("/api/v1/samples"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }

    @Test
    void testCreateSample_DatabasePersistence() throws Exception {
        // Create a sample
        String responseBody = mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "text": "Persistence Test",
                                    "number": 777,
                                    "status": "active"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract ID from response (simple approach)
        String id = org.springframework.boot.json.JsonParserFactory.getJsonParser()
                .parseMap(responseBody)
                .get("id")
                .toString();

        // Verify it appears in the list
        mockMvc.perform(get("/api/v1/samples"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.number == 777)].text").value("Persistence Test"));

        // Test GET by ID endpoint
        mockMvc.perform(get("/api/v1/samples/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.text").value("Persistence Test"))
                .andExpect(jsonPath("$.number").value(777));
    }

    @Test
    void testGetSample_NotFound() throws Exception {
        // Try to get non-existent sample
        mockMvc.perform(get("/api/v1/samples/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Sample not found with ID: 99999"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
