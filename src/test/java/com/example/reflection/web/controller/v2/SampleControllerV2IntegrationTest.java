package com.example.reflection.web.controller.v2;

import com.example.reflection.AbstractIntegrationTest;
import com.example.reflection.OpenApiValidator;
import com.example.reflection.domain.model.Status;
import com.example.reflection.persistence.repository.SampleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for SampleControllerV2.
 * Tests V2 API with breaking changes:
 * - New structured metadata format
 * - Priority field support
 * - Statistics aggregation endpoint
 * - OpenAPI schema validation
 */
class SampleControllerV2IntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SampleRepository repository;

    @BeforeEach
    void setUp() {
        // Clean database before each test
        repository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v2/samples - Create sample with priority")
    void testCreateSample_WithPriority() throws Exception {
        String requestBody = """
            {
                "text": "Test Sample V2",
                "number": 42,
                "status": "active",
                "priority": 8
            }
            """;

        mockMvc.perform(post("/api/v2/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(OpenApiValidator.matchesOpenApi("openapi-v1.json", "POST", "/api/v2/samples", 201))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.text").value("Test Sample V2"))
            .andExpect(jsonPath("$.number").value(42))
            .andExpect(jsonPath("$.status").value("active"))
            .andExpect(jsonPath("$.priority").value(8))
            .andExpect(jsonPath("$.metadata").exists())
            .andExpect(jsonPath("$.metadata.totalValue").value(0))
            .andExpect(jsonPath("$.metadata.itemCount").value(0))
            .andExpect(jsonPath("$.metadata.tags").isArray())
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("POST /api/v2/samples - Create sample without priority defaults to 5")
    void testCreateSample_DefaultPriority() throws Exception {
        String requestBody = """
            {
                "text": "Default Priority Sample",
                "number": 100,
                "status": "inactive"
            }
            """;

        mockMvc.perform(post("/api/v2/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(OpenApiValidator.matchesOpenApi("openapi-v1.json", "POST", "/api/v2/samples", 201))
            .andExpect(jsonPath("$.priority").value(5));
    }

    @Test
    @DisplayName("POST /api/v2/samples - Validation error for invalid priority")
    void testCreateSample_InvalidPriority() throws Exception {
        String requestBody = """
            {
                "text": "Invalid Priority",
                "number": 50,
                "status": "active",
                "priority": 15
            }
            """;

        mockMvc.perform(post("/api/v2/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Validation failed"))
            .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("GET /api/v2/samples - List samples returns V2 format")
    void testListSamples_V2Format() throws Exception {
        // Create sample via V2 endpoint
        String requestBody = """
            {
                "text": "Sample for List",
                "number": 77,
                "status": "active",
                "priority": 3
            }
            """;

        mockMvc.perform(post("/api/v2/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated());

        // List samples
        mockMvc.perform(get("/api/v2/samples"))
            .andExpect(status().isOk())
            .andExpect(OpenApiValidator.matchesOpenApi("openapi-v1.json", "GET", "/api/v2/samples", 200))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].text").value("Sample for List"))
            .andExpect(jsonPath("$[0].priority").value(3))
            .andExpect(jsonPath("$[0].metadata").exists())
            .andExpect(jsonPath("$[0].metadata.totalValue").exists())
            .andExpect(jsonPath("$[0].metadata.itemCount").exists())
            .andExpect(jsonPath("$[0].metadata.tags").isArray());
    }

    @Test
    @DisplayName("GET /api/v2/samples/{id} - Get sample by ID in V2 format")
    void testGetSampleById_V2Format() throws Exception {
        // Create sample
        String requestBody = """
            {
                "text": "Single Sample V2",
                "number": 99,
                "status": "active",
                "priority": 7
            }
            """;

        var result = mockMvc.perform(post("/api/v2/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andReturn();

        // Extract ID from created response
        String createResponse = result.getResponse().getContentAsString();
        // Parse JSON to get ID (simple parsing)
        long id = Long.parseLong(createResponse.split("\"id\":")[1].split(",")[0]);

        // Get by ID
        mockMvc.perform(get("/api/v2/samples/" + id))
            .andExpect(status().isOk())
            .andExpect(OpenApiValidator.matchesOpenApi("openapi-v1.json", "GET", "/api/v2/samples/{id}", 200))
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.text").value("Single Sample V2"))
            .andExpect(jsonPath("$.priority").value(7))
            .andExpect(jsonPath("$.metadata").exists());
    }

    @Test
    @DisplayName("GET /api/v2/samples/{id} - Returns 404 for non-existent sample")
    void testGetSampleById_NotFound() throws Exception {
        mockMvc.perform(get("/api/v2/samples/99999"))
            .andExpect(status().isNotFound())
            .andExpect(OpenApiValidator.matchesOpenApi("openapi-v1.json", "GET", "/api/v2/samples/{id}", 404))
            .andExpect(jsonPath("$.message").value("Sample not found with ID: 99999"));
    }

    @Test
    @DisplayName("GET /api/v2/samples/statistics - Returns empty statistics")
    void testGetStatistics_Empty() throws Exception {
        mockMvc.perform(get("/api/v2/samples/statistics"))
            .andExpect(status().isOk())
            .andExpect(OpenApiValidator.matchesOpenApi("openapi-v1.json", "GET", "/api/v2/samples/statistics", 200))
            .andExpect(jsonPath("$.totalCount").value(0))
            .andExpect(jsonPath("$.averageNumber").value(0.0))
            .andExpect(jsonPath("$.totalNumberSum").value(0))
            .andExpect(jsonPath("$.countByStatus").isEmpty())
            .andExpect(jsonPath("$.averagePriority").value(5.0));
    }

    @Test
    @DisplayName("GET /api/v2/samples/statistics - Calculates correct aggregates")
    void testGetStatistics_WithData() throws Exception {
        // Create multiple samples with different values
        createSample("Sample 1", 100, "active", 8);
        createSample("Sample 2", 200, "active", 6);
        createSample("Sample 3", 150, "inactive", 4);

        mockMvc.perform(get("/api/v2/samples/statistics"))
            .andExpect(status().isOk())
            .andExpect(OpenApiValidator.matchesOpenApi("openapi-v1.json", "GET", "/api/v2/samples/statistics", 200))
            .andExpect(jsonPath("$.totalCount").value(3))
            .andExpect(jsonPath("$.averageNumber").value(150.0))
            .andExpect(jsonPath("$.totalNumberSum").value(450))
            .andExpect(jsonPath("$.countByStatus.ACTIVE").value(2))
            .andExpect(jsonPath("$.countByStatus.INACTIVE").value(1))
            .andExpect(jsonPath("$.averagePriority").value(6.0));
    }

    @Test
    @DisplayName("GET /api/v2/samples/statistics - Handles mixed priorities")
    void testGetStatistics_MixedPriorities() throws Exception {
        createSample("High Priority", 50, "active", 10);
        createSample("Low Priority", 50, "active", 1);
        createSample("Default Priority", 50, "active", null); // Should default to 5

        mockMvc.perform(get("/api/v2/samples/statistics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalCount").value(3))
            .andExpect(jsonPath("$.averageNumber").value(50.0))
            .andExpect(jsonPath("$.averagePriority").value(closeTo(5.33, 0.1))); // (10+1+5)/3 ≈ 5.33
    }

    @Test
    @DisplayName("V2 response structure differs from V1 - Breaking change validation")
    void testV2BreakingChanges() throws Exception {
        String requestBody = """
            {
                "text": "Breaking Change Test",
                "number": 42,
                "status": "active",
                "priority": 5
            }
            """;

        mockMvc.perform(post("/api/v2/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            // V2 has 'metadata' instead of 'extras'
            .andExpect(jsonPath("$.metadata").exists())
            .andExpect(jsonPath("$.extras").doesNotExist())
            // V2 has 'priority' field
            .andExpect(jsonPath("$.priority").exists())
            // V2 metadata is structured, not a simple map
            .andExpect(jsonPath("$.metadata.totalValue").exists())
            .andExpect(jsonPath("$.metadata.itemCount").exists())
            .andExpect(jsonPath("$.metadata.tags").isArray());
    }

    // Helper method to create samples
    private void createSample(String text, int number, String status, Integer priority) throws Exception {
        String priorityField = priority != null ? ",\"priority\": " + priority : "";
        String requestBody = String.format("""
            {
                "text": "%s",
                "number": %d,
                "status": "%s"%s
            }
            """, text, number, status, priorityField);

        mockMvc.perform(post("/api/v2/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated());
    }
}
