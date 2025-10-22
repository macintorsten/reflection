package com.example.reflection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for SampleController using Testcontainers for PostgreSQL
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class SampleControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SampleRepository repository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        repository.deleteAll();
    }

    @Test
    void testCreateSample() throws Exception {
        // Arrange
        SampleDTO dto = new SampleDTO();
        dto.text = "Test Sample";
        dto.number = 42;
        dto.status = Sample.Status.ACTIVE;

        // Act & Assert
        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text", is("Test Sample")))
                .andExpect(jsonPath("$.number", is(42)))
                .andExpect(jsonPath("$.status", is("active")));
    }

    @Test
    void testCreateSampleWithExtras() throws Exception {
        // Arrange
        SampleDTO dto = new SampleDTO();
        dto.text = "Sample with extras";
        dto.number = 100;
        dto.status = Sample.Status.INACTIVE;
        
        Map<String, String> extras = new HashMap<>();
        extras.put("priority", "5");
        extras.put("category", "10");
        dto.extras = extras;

        // Act & Assert
        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is("Sample with extras")))
                .andExpect(jsonPath("$.number", is(100)))
                .andExpect(jsonPath("$.status", is("inactive")))
                .andExpect(jsonPath("$.extras.priority", is("5")))
                .andExpect(jsonPath("$.extras.category", is("10")));
    }

    @Test
    void testCreateSampleValidationFailure() throws Exception {
        // Arrange - text too short (less than 3 characters)
        SampleDTO dto = new SampleDTO();
        dto.text = "Hi"; // Too short
        dto.number = 50;
        dto.status = Sample.Status.ACTIVE;

        // Act & Assert
        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateSampleNumberOutOfRange() throws Exception {
        // Arrange - number out of range
        SampleDTO dto = new SampleDTO();
        dto.text = "Test Sample";
        dto.number = 1500; // Too large (max is 1000)
        dto.status = Sample.Status.ACTIVE;

        // Act & Assert
        mockMvc.perform(post("/api/samples")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListSamples() throws Exception {
        // Arrange - Create some samples first
        SampleDTO dto1 = new SampleDTO();
        dto1.text = "First Sample";
        dto1.number = 10;
        dto1.status = Sample.Status.ACTIVE;

        SampleDTO dto2 = new SampleDTO();
        dto2.text = "Second Sample";
        dto2.number = 20;
        dto2.status = Sample.Status.INACTIVE;

        // Create samples
        mockMvc.perform(post("/api/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto1)));

        mockMvc.perform(post("/api/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto2)));

        // Act & Assert - List all samples
        mockMvc.perform(get("/api/samples"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].text", is("First Sample")))
                .andExpect(jsonPath("$[0].number", is(10)))
                .andExpect(jsonPath("$[0].status", is("active")))
                .andExpect(jsonPath("$[1].text", is("Second Sample")))
                .andExpect(jsonPath("$[1].number", is(20)))
                .andExpect(jsonPath("$[1].status", is("inactive")));
    }

    @Test
    void testListSamplesWithExtras() throws Exception {
        // NOTE: Currently extras (mapField) is marked @Transient in Sample entity
        // so it's NOT persisted to the database. This test verifies that behavior.
        
        // Arrange
        SampleDTO dto = new SampleDTO();
        dto.text = "Sample with data";
        dto.number = 999;
        dto.status = Sample.Status.ACTIVE;
        
        Map<String, String> extras = new HashMap<>();
        extras.put("version", "2");
        extras.put("level", "99");
        dto.extras = extras;

        // Create sample
        mockMvc.perform(post("/api/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.extras.version", is("2")))
                .andExpect(jsonPath("$.extras.level", is("99")));

        // Act & Assert - Extras are NOT persisted due to @Transient annotation
        mockMvc.perform(get("/api/samples"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].text", is("Sample with data")))
                .andExpect(jsonPath("$[0].extras").doesNotExist()); // Extras not persisted
    }

    @Test
    void testListEmptySamples() throws Exception {
        // Act & Assert - List when database is empty
        mockMvc.perform(get("/api/samples"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testCreateMultipleSamplesAndList() throws Exception {
        // Arrange - Create 5 samples
        for (int i = 1; i <= 5; i++) {
            SampleDTO dto = new SampleDTO();
            dto.text = "Sample " + i;
            dto.number = i * 10;
            dto.status = i % 2 == 0 ? Sample.Status.INACTIVE : Sample.Status.ACTIVE;

            mockMvc.perform(post("/api/samples")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)));
        }

        // Act & Assert - Verify all 5 samples are returned
        mockMvc.perform(get("/api/samples"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].number", is(10)))
                .andExpect(jsonPath("$[1].number", is(20)))
                .andExpect(jsonPath("$[2].number", is(30)))
                .andExpect(jsonPath("$[3].number", is(40)))
                .andExpect(jsonPath("$[4].number", is(50)));
    }

    @Test
    void testDatabasePersistence() throws Exception {
        // Arrange & Act - Create a sample
        SampleDTO dto = new SampleDTO();
        dto.text = "Persistent Sample";
        dto.number = 777;
        dto.status = Sample.Status.ACTIVE;

        mockMvc.perform(post("/api/samples")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // Assert - Verify it was saved to database
        mockMvc.perform(get("/api/samples"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].text", is("Persistent Sample")))
                .andExpect(jsonPath("$[0].number", is(777)));

        // Verify using repository directly
        var samples = repository.findAll();
        assert samples.size() == 1;
        assert samples.get(0).text.equals("Persistent Sample");
        assert samples.get(0).number == 777;
    }
}
