package {{PACKAGE}};

import com.example.reflection.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test template for {{CLASS_NAME}}
 * 
 * This is an integration test that tests the full Spring Boot application
 * with real dependencies including database (using Testcontainers).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class {{CLASS_NAME}}IntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private {{REPOSITORY}} repository; // TODO: Add required repositories

    @BeforeEach
    void setUp() {
        // Clean database before each test
        repository.deleteAll();
        
        // Setup test data if needed
    }

    @Test
    void shouldReturnEmptyListWhenNoDataExists() throws Exception {
        // When & Then - Request data from empty database
        mockMvc.perform(get("/api/{{ENDPOINT}}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldCreateResourceSuccessfully() throws Exception {
        // Given - Prepare request body
        String requestBody = """
            {
                "field1": "value1",
                "field2": "value2"
            }
            """;
        
        // When & Then - Create resource and verify response
        mockMvc.perform(post("/api/{{ENDPOINT}}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.field1").value("value1"))
            .andExpect(jsonPath("$.field2").value("value2"))
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldReturnResourceWhenExists() throws Exception {
        // Given - Create test data in database
        var entity = createTestEntity();
        var saved = repository.save(entity);
        
        // When & Then - Retrieve resource
        mockMvc.perform(get("/api/{{ENDPOINT}}/" + saved.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(saved.getId()));
    }

    @Test
    void shouldReturnNotFoundWhenResourceDoesNotExist() throws Exception {
        // When & Then - Request non-existent resource
        mockMvc.perform(get("/api/{{ENDPOINT}}/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateResourceSuccessfully() throws Exception {
        // Given - Create existing resource
        var entity = createTestEntity();
        var saved = repository.save(entity);
        
        String updateBody = """
            {
                "field1": "updated value"
            }
            """;
        
        // When & Then - Update resource
        mockMvc.perform(put("/api/{{ENDPOINT}}/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.field1").value("updated value"));
    }

    @Test
    void shouldDeleteResourceSuccessfully() throws Exception {
        // Given - Create existing resource
        var entity = createTestEntity();
        var saved = repository.save(entity);
        
        // When - Delete resource
        mockMvc.perform(delete("/api/{{ENDPOINT}}/" + saved.getId()))
            .andExpect(status().isNoContent());
        
        // Then - Verify resource is deleted
        mockMvc.perform(get("/api/{{ENDPOINT}}/" + saved.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        // Given - Invalid request body
        String invalidBody = """
            {
                "field1": null,
                "field2": ""
            }
            """;
        
        // When & Then - Attempt to create with invalid data
        mockMvc.perform(post("/api/{{ENDPOINT}}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFilterResourcesCorrectly() throws Exception {
        // Given - Create multiple test entities
        repository.save(createTestEntity("value1"));
        repository.save(createTestEntity("value2"));
        
        // When & Then - Filter resources
        mockMvc.perform(get("/api/{{ENDPOINT}}?filter=value1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].field1").value("value1"));
    }

    // Helper methods
    
    private Object createTestEntity() {
        // TODO: Create and return test entity
        return new Object();
    }
    
    private Object createTestEntity(String value) {
        // TODO: Create and return test entity with specific value
        return new Object();
    }
}
