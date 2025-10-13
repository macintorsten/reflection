package com.example.reflection;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.core.util.Json;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void testGenerateFullOpenAPISpec() throws Exception {
        // Generate schemas for Sample class
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(Sample.class);
        Components components = new Components();
        for (Map.Entry<String, Schema> entry : schemas.entrySet()) {
            components.addSchemas(entry.getKey(), entry.getValue());
        }
        OpenAPI openAPI = new OpenAPI().components(components);
        String json = Json.mapper().writerWithDefaultPrettyPrinter().writeValueAsString(openAPI);
        System.out.println("OpenAPI JSON:\n" + json);
        assertTrue(json.contains("components"));
    }
}
