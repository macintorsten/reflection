#!/usr/bin/env bash
set -euo pipefail

# generate-test-scaffold.sh - Generate test file scaffold based on component type
# Usage: ./generate-test-scaffold.sh <componentName> <componentType> [testType]

if [ $# -lt 2 ]; then
    echo "Usage: $0 <componentName> <componentType> [testType]" >&2
    echo "  componentName: Name of the component (e.g., UserService)" >&2
    echo "  componentType: service, controller, repository, entity, mapper" >&2
    echo "  testType: unit (default) or integration" >&2
    echo "" >&2
    echo "Example: $0 UserService service unit" >&2
    exit 1
fi

COMPONENT_NAME="$1"
COMPONENT_TYPE="$2"
TEST_TYPE="${3:-unit}"

PROJECT_ROOT="${PROJECT_ROOT:-$(git rev-parse --show-toplevel 2>/dev/null || pwd)}"
SRC_MAIN="${PROJECT_ROOT}/src/main/java"
SRC_TEST="${PROJECT_ROOT}/src/test/java"

# Find the source file
SOURCE_FILE=$(find "$SRC_MAIN" -name "${COMPONENT_NAME}.java" | head -1)

if [ -z "$SOURCE_FILE" ]; then
    echo "Warning: Source file ${COMPONENT_NAME}.java not found in $SRC_MAIN" >&2
    echo "Will generate test scaffold in default location" >&2
    
    # Try to find package from other files of same type
    case "$COMPONENT_TYPE" in
        service)
            PACKAGE_PATH=$(find "$SRC_MAIN" -name "*Service.java" | head -1 | xargs dirname 2>/dev/null || echo "")
            ;;
        controller)
            PACKAGE_PATH=$(find "$SRC_MAIN" -name "*Controller.java" | head -1 | xargs dirname 2>/dev/null || echo "")
            ;;
        repository)
            PACKAGE_PATH=$(find "$SRC_MAIN" -name "*Repository.java" | head -1 | xargs dirname 2>/dev/null || echo "")
            ;;
        *)
            PACKAGE_PATH=""
            ;;
    esac
    
    if [ -n "$PACKAGE_PATH" ]; then
        PACKAGE=$(echo "$PACKAGE_PATH" | sed "s|$SRC_MAIN/||" | tr '/' '.')
    else
        PACKAGE="com.example.reflection"
        echo "Using default package: $PACKAGE" >&2
    fi
else
    # Extract package from source file
    PACKAGE=$(grep "^package " "$SOURCE_FILE" | sed 's/package //; s/;//' | tr -d ' ')
    echo "Found source file: $SOURCE_FILE" >&2
    echo "Package: $PACKAGE" >&2
fi

# Determine test file name and path
if [ "$TEST_TYPE" = "integration" ]; then
    TEST_CLASS_NAME="${COMPONENT_NAME}IntegrationTest"
else
    TEST_CLASS_NAME="${COMPONENT_NAME}Test"
fi

PACKAGE_PATH=$(echo "$PACKAGE" | tr '.' '/')
TEST_FILE_PATH="${SRC_TEST}/${PACKAGE_PATH}/${TEST_CLASS_NAME}.java"

# Check if test file already exists
if [ -f "$TEST_FILE_PATH" ]; then
    echo "Error: Test file already exists at $TEST_FILE_PATH" >&2
    echo "Please edit the existing file or delete it first" >&2
    exit 1
fi

# Create directory if it doesn't exist
mkdir -p "$(dirname "$TEST_FILE_PATH")"

# Generate test scaffold based on type
echo "Generating $TEST_TYPE test scaffold for $COMPONENT_NAME ($COMPONENT_TYPE)" >&2

case "$COMPONENT_TYPE" in
    service)
        if [ "$TEST_TYPE" = "integration" ]; then
            # Service integration test
            cat > "$TEST_FILE_PATH" << EOF
package ${PACKAGE};

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ${TEST_CLASS_NAME} {

    @Autowired
    private ${COMPONENT_NAME} ${COMPONENT_NAME,,};

    @Test
    void contextLoads() {
        assertThat(${COMPONENT_NAME,,}).isNotNull();
    }

    @Test
    void shouldPerformOperationSuccessfully() {
        // Given - Setup test data
        
        // When - Execute the operation
        
        // Then - Verify the outcome
        
    }
}
EOF
        else
            # Service unit test
            cat > "$TEST_FILE_PATH" << EOF
package ${PACKAGE};

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ${TEST_CLASS_NAME} {

    @Mock
    private Object dependency; // TODO: Replace with actual dependencies

    @InjectMocks
    private ${COMPONENT_NAME} ${COMPONENT_NAME,,};

    @BeforeEach
    void setUp() {
        // Setup common test data
    }

    @Test
    void shouldReturnExpectedResultWhenValidInput() {
        // Given - Setup test data and mocks
        
        // When - Execute the method under test
        
        // Then - Verify the outcome
        
    }

    @Test
    void shouldThrowExceptionWhenInvalidInput() {
        // Given - Setup invalid input
        
        // When & Then - Verify exception is thrown
        assertThatThrownBy(() -> {
            // Call method with invalid input
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
EOF
        fi
        ;;
        
    controller)
        if [ "$TEST_TYPE" = "integration" ]; then
            # Controller integration test
            cat > "$TEST_FILE_PATH" << EOF
package ${PACKAGE};

import com.example.reflection.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ${TEST_CLASS_NAME} extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnSuccessWhenValidRequest() throws Exception {
        // Given - Setup request data
        String requestBody = """
            {
                "field": "value"
            }
            """;
        
        // When & Then - Execute request and verify response
        mockMvc.perform(post("/api/endpoint")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("\$.field").value("value"));
    }

    @Test
    void shouldReturnNotFoundWhenResourceDoesNotExist() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/endpoint/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenInvalidData() throws Exception {
        // Given - Setup invalid request data
        String invalidRequest = """
            {
                "field": null
            }
            """;
        
        // When & Then
        mockMvc.perform(post("/api/endpoint")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
            .andExpect(status().isBadRequest());
    }
}
EOF
        else
            # Controller unit test
            cat > "$TEST_FILE_PATH" << EOF
package ${PACKAGE};

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ${TEST_CLASS_NAME} {

    @Mock
    private Object service; // TODO: Replace with actual service dependency

    @InjectMocks
    private ${COMPONENT_NAME} controller;

    @Test
    void shouldReturnOkWhenValidRequest() {
        // Given - Setup test data and mocks
        
        // When - Call controller method
        ResponseEntity<?> response = controller.handleRequest(null);
        
        // Then - Verify response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnNotFoundWhenResourceDoesNotExist() {
        // Given
        
        // When
        ResponseEntity<?> response = controller.getById(999L);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
EOF
        fi
        ;;
        
    repository)
        # Repository test (always integration)
        cat > "$TEST_FILE_PATH" << EOF
package ${PACKAGE};

import com.example.reflection.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ${TEST_CLASS_NAME} {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ${COMPONENT_NAME} repository;

    @BeforeEach
    void setUp() {
        // Setup test data
    }

    @Test
    void shouldSaveAndRetrieveEntity() {
        // Given - Create entity
        
        // When - Save entity
        
        // Then - Verify entity is saved and retrievable
        
    }

    @Test
    void shouldFindByCustomQuery() {
        // Given - Setup test data
        
        // When - Execute custom query
        
        // Then - Verify results
        
    }
}
EOF
        ;;
        
    mapper)
        # Mapper unit test
        cat > "$TEST_FILE_PATH" << EOF
package ${PACKAGE};

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ${TEST_CLASS_NAME} {

    private ${COMPONENT_NAME} mapper;

    @BeforeEach
    void setUp() {
        mapper = new ${COMPONENT_NAME}();
    }

    @Test
    void shouldMapToDto() {
        // Given - Create entity
        
        // When - Map to DTO
        
        // Then - Verify DTO fields
        
    }

    @Test
    void shouldMapToEntity() {
        // Given - Create DTO
        
        // When - Map to entity
        
        // Then - Verify entity fields
        
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        
        // When
        
        // Then - Verify null handling
        
    }
}
EOF
        ;;
        
    entity|*)
        # Generic unit test
        cat > "$TEST_FILE_PATH" << EOF
package ${PACKAGE};

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ${TEST_CLASS_NAME} {

    @Test
    void shouldCreateInstanceSuccessfully() {
        // Given - Setup test data
        
        // When - Create instance
        
        // Then - Verify instance state
        
    }

    @Test
    void shouldValidateBusinessRules() {
        // Given
        
        // When
        
        // Then
        
    }
}
EOF
        ;;
esac

echo "" >&2
echo "✓ Generated test file: $TEST_FILE_PATH" >&2
echo "" >&2
echo "Next steps:" >&2
echo "  1. Edit $TEST_FILE_PATH" >&2
echo "  2. Replace TODO items with actual dependencies" >&2
echo "  3. Write failing test (RED phase)" >&2
echo "  4. Run test: mvn test -Dtest=${TEST_CLASS_NAME}" >&2
echo "" >&2

exit 0
