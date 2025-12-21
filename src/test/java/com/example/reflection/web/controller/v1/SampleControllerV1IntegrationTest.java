package com.example.reflection.web.controller.v1;

import com.example.reflection.AbstractIntegrationTest;
import com.example.reflection.OpenApiValidator;
import com.example.reflection.domain.model.Status;
import com.example.reflection.persistence.repository.SampleRepository;
import com.example.reflection.web.dto.v1.request.CreateSampleRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack integration tests for V1 API with real PostgreSQL database (via Testcontainers).
 * Tests the entire stack: HTTP → Controller → Service → Repository → Database
 */
class SampleControllerV1IntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SampleRepository repository;

    @BeforeEach
    void setUp() {
        // Ensure test isolation by cleaning database before each test
        repository.deleteAll();
    }

    @Test
    void testCreateSample_FullStack_ValidatesSchema() throws Exception {
        var request = new CreateSampleRequest("Integration Test", 100, Status.ACTIVE, null);

        mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(OpenApiValidator.matchesOpenApi("openapi.json", "POST", "/api/v1/samples", 201))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").value("Integration Test"))
                .andExpect(jsonPath("$.number").value(100))
                .andExpect(jsonPath("$.status").value("active"));
    }

    @Test
    void testCreateSample_WithExtras_FullStack() throws Exception {
        var request = new CreateSampleRequest(
            "Test With Extras", 
            42, 
            Status.ACTIVE, 
            Map.of("key1", "123", "key2", "456")
        );

        mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(OpenApiValidator.matchesOpenApi("openapi.json", "POST", "/api/v1/samples", 201))
                .andExpect(jsonPath("$.extras.key1").value("123"))
                .andExpect(jsonPath("$.extras.key2").value("456"));
    }

    @Test
    void testCreateSample_ValidationFailure_TextTooShort() throws Exception {
        // Keep JSON string for validation edge case
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
                .andExpect(OpenApiValidator.matchesOpenApi("openapi.json", "POST", "/api/v1/samples", 400))
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void testCreateSample_ValidationFailure_NumberOutOfRange() throws Exception {
        // Keep JSON string for validation edge case
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
                .andExpect(OpenApiValidator.matchesOpenApi("openapi.json", "POST", "/api/v1/samples", 400))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("number"));
    }

    @Test
    void testCreateSample_ValidationFailure_InvalidStatus() throws Exception {
        // Keep JSON string for validation edge case - invalid enum
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
                .andExpect(OpenApiValidator.matchesOpenApi("openapi.json", "POST", "/api/v1/samples", 400));
    }

    @Test
    void testListSamples_EmptyDatabase() throws Exception {
        mockMvc.perform(get("/api/v1/samples"))
                .andExpect(status().isOk())
                .andExpect(OpenApiValidator.matchesOpenApi("openapi.json", "GET", "/api/v1/samples", 200));
    }

    @Test
    void testListSamples_AfterCreation() throws Exception {
        // Create a sample using DTO
        var request = new CreateSampleRequest("List Test Sample", 999, Status.INACTIVE, null);
        mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // List should include the created sample
        mockMvc.perform(get("/api/v1/samples"))
                .andExpect(status().isOk())
                .andExpect(OpenApiValidator.matchesOpenApi("openapi.json", "GET", "/api/v1/samples", 200))
                .andExpect(jsonPath("$[?(@.number == 999)].text").value("List Test Sample"));
    }

    @Test
    void testListSamples_MultipleItems() throws Exception {
        // Create multiple samples using DTOs
        var request1 = new CreateSampleRequest("First Sample", 10, Status.ACTIVE, null);
        mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        var request2 = new CreateSampleRequest("Second Sample", 20, Status.INACTIVE, Map.of("tag", "1"));
        mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        // List all and validate
        mockMvc.perform(get("/api/v1/samples"))
                .andExpect(status().isOk())
                .andExpect(OpenApiValidator.matchesOpenApi("openapi.json", "GET", "/api/v1/samples", 200))
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }

    @Test
    void testCreateSample_DatabasePersistence() throws Exception {
        // Create a sample using helper method
        var request = new CreateSampleRequest("Persistence Test", 777, Status.ACTIVE, null);
        Long id = createSampleAndGetId(request);

        // Verify it appears in the list
        mockMvc.perform(get("/api/v1/samples"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.number == 777)].text").value("Persistence Test"));

        // Test GET by ID endpoint
        mockMvc.perform(get("/api/v1/samples/" + id))
                .andExpect(status().isOk())
                .andExpect(OpenApiValidator.matchesOpenApi("openapi.json", "GET", "/api/v1/samples/{id}", 200))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.text").value("Persistence Test"))
                .andExpect(jsonPath("$.number").value(777));
    }

    @Test
    void testGetSample_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/samples/99999"))
                .andExpect(status().isNotFound())
                .andExpect(OpenApiValidator.matchesOpenApi("openapi.json", "GET", "/api/v1/samples/{id}", 404))
                .andExpect(jsonPath("$.message").value("Sample not found with ID: 99999"));
    }

    // ========== Helper Methods ==========

    /**
     * Helper to create a sample and return its ID.
     */
    private Long createSampleAndGetId(CreateSampleRequest request) throws Exception {
        String responseBody = mockMvc.perform(post("/api/v1/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return Long.parseLong(
            org.springframework.boot.json.JsonParserFactory.getJsonParser()
                .parseMap(responseBody)
                .get("id")
                .toString()
        );
    }
}
