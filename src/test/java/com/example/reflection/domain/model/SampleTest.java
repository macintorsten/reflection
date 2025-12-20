package com.example.reflection.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for Sample domain model.
 * Tests business logic, validation, and builder patterns.
 */
class SampleTest {

    @Test
    void shouldCreateSampleWithAllFields() {
        // Given
        Long id = 1L;
        String text = "test text";
        Integer number = 42;
        Status status = Status.ACTIVE;
        Map<String, Integer> mapField = Map.of("key1", 100);
        LocalDateTime now = LocalDateTime.now();

        // When
        Sample sample = new Sample(id, text, number, status, mapField, now, now);

        // Then
        assertThat(sample.getId()).isEqualTo(id);
        assertThat(sample.getText()).isEqualTo(text);
        assertThat(sample.getNumber()).isEqualTo(number);
        assertThat(sample.getStatus()).isEqualTo(status);
        assertThat(sample.getMapField()).isEqualTo(mapField);
        assertThat(sample.getCreatedAt()).isEqualTo(now);
        assertThat(sample.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void shouldCreateEmptySampleWithNoArgsConstructor() {
        // When
        Sample sample = new Sample();

        // Then
        assertThat(sample.getId()).isNull();
        assertThat(sample.getText()).isNull();
        assertThat(sample.getNumber()).isNull();
        assertThat(sample.getStatus()).isNull();
        assertThat(sample.getMapField()).isNull();
        assertThat(sample.getCreatedAt()).isNull();
        assertThat(sample.getUpdatedAt()).isNull();
    }

    @Test
    void shouldSetFieldsUsingSetters() {
        // Given
        Sample sample = new Sample();
        Long id = 1L;
        String text = "updated text";
        Integer number = 99;
        Status status = Status.INACTIVE;
        Map<String, Integer> mapField = Map.of("key", 50);
        LocalDateTime now = LocalDateTime.now();

        // When
        sample.setId(id);
        sample.setText(text);
        sample.setNumber(number);
        sample.setStatus(status);
        sample.setMapField(mapField);
        sample.setCreatedAt(now);
        sample.setUpdatedAt(now);

        // Then
        assertThat(sample.getId()).isEqualTo(id);
        assertThat(sample.getText()).isEqualTo(text);
        assertThat(sample.getNumber()).isEqualTo(number);
        assertThat(sample.getStatus()).isEqualTo(status);
        assertThat(sample.getMapField()).isEqualTo(mapField);
        assertThat(sample.getCreatedAt()).isEqualTo(now);
        assertThat(sample.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void shouldReturnTrueWhenStatusIsActive() {
        // Given
        Sample sample = new Sample();
        sample.setStatus(Status.ACTIVE);

        // When
        boolean isActive = sample.isActive();

        // Then
        assertThat(isActive).isTrue();
    }

    @Test
    void shouldReturnFalseWhenStatusIsInactive() {
        // Given
        Sample sample = new Sample();
        sample.setStatus(Status.INACTIVE);

        // When
        boolean isActive = sample.isActive();

        // Then
        assertThat(isActive).isFalse();
    }

    @Test
    void shouldReturnFalseWhenStatusIsNull() {
        // Given
        Sample sample = new Sample();
        sample.setStatus(null);

        // When
        boolean isActive = sample.isActive();

        // Then
        assertThat(isActive).isFalse();
    }

    @Test
    void shouldValidateSuccessfullyWithValidData() {
        // Given
        Sample sample = new Sample();
        sample.setText("valid text");
        sample.setNumber(500);

        // When / Then
        sample.validate(); // Should not throw
    }

    @Test
    void shouldValidateWithMinimumTextLength() {
        // Given
        Sample sample = new Sample();
        sample.setText("abc"); // Exactly 3 characters
        sample.setNumber(0);

        // When / Then
        sample.validate(); // Should not throw
    }

    @Test
    void shouldValidateWithMaximumTextLength() {
        // Given
        Sample sample = new Sample();
        sample.setText("a".repeat(100)); // Exactly 100 characters
        sample.setNumber(1000);

        // When / Then
        sample.validate(); // Should not throw
    }

    @Test
    void shouldValidateWithMinimumNumber() {
        // Given
        Sample sample = new Sample();
        sample.setText("valid text");
        sample.setNumber(0); // Minimum valid number

        // When / Then
        sample.validate(); // Should not throw
    }

    @Test
    void shouldValidateWithMaximumNumber() {
        // Given
        Sample sample = new Sample();
        sample.setText("valid text");
        sample.setNumber(1000); // Maximum valid number

        // When / Then
        sample.validate(); // Should not throw
    }

    @Test
    void shouldThrowExceptionWhenTextIsNull() {
        // Given
        Sample sample = new Sample();
        sample.setText(null);
        sample.setNumber(100);

        // When / Then
        assertThatThrownBy(() -> sample.validate())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Text must be between 3 and 100 characters");
    }

    @Test
    void shouldThrowExceptionWhenTextIsTooShort() {
        // Given
        Sample sample = new Sample();
        sample.setText("ab"); // Only 2 characters
        sample.setNumber(100);

        // When / Then
        assertThatThrownBy(() -> sample.validate())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Text must be between 3 and 100 characters");
    }

    @Test
    void shouldThrowExceptionWhenTextIsTooLong() {
        // Given
        Sample sample = new Sample();
        sample.setText("a".repeat(101)); // 101 characters
        sample.setNumber(100);

        // When / Then
        assertThatThrownBy(() -> sample.validate())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Text must be between 3 and 100 characters");
    }

    @Test
    void shouldThrowExceptionWhenNumberIsNull() {
        // Given
        Sample sample = new Sample();
        sample.setText("valid text");
        sample.setNumber(null);

        // When / Then
        assertThatThrownBy(() -> sample.validate())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Number must be between 0 and 1000");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10, -100})
    void shouldThrowExceptionWhenNumberIsNegative(int negativeNumber) {
        // Given
        Sample sample = new Sample();
        sample.setText("valid text");
        sample.setNumber(negativeNumber);

        // When / Then
        assertThatThrownBy(() -> sample.validate())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Number must be between 0 and 1000");
    }

    @ParameterizedTest
    @ValueSource(ints = {1001, 1100, 2000})
    void shouldThrowExceptionWhenNumberIsTooLarge(int largeNumber) {
        // Given
        Sample sample = new Sample();
        sample.setText("valid text");
        sample.setNumber(largeNumber);

        // When / Then
        assertThatThrownBy(() -> sample.validate())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Number must be between 0 and 1000");
    }

    @Test
    void shouldBuildSampleWithBuilder() {
        // Given
        Long id = 1L;
        String text = "builder test";
        Integer number = 42;
        Status status = Status.ACTIVE;
        Map<String, Integer> mapField = Map.of("key", 100);
        LocalDateTime now = LocalDateTime.now();

        // When
        Sample sample = Sample.builder()
                .id(id)
                .text(text)
                .number(number)
                .status(status)
                .mapField(mapField)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Then
        assertThat(sample.getId()).isEqualTo(id);
        assertThat(sample.getText()).isEqualTo(text);
        assertThat(sample.getNumber()).isEqualTo(number);
        assertThat(sample.getStatus()).isEqualTo(status);
        assertThat(sample.getMapField()).isEqualTo(mapField);
        assertThat(sample.getCreatedAt()).isEqualTo(now);
        assertThat(sample.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void shouldBuildSampleWithPartialFields() {
        // When
        Sample sample = Sample.builder()
                .text("partial builder")
                .number(10)
                .build();

        // Then
        assertThat(sample.getText()).isEqualTo("partial builder");
        assertThat(sample.getNumber()).isEqualTo(10);
        assertThat(sample.getId()).isNull();
        assertThat(sample.getStatus()).isNull();
        assertThat(sample.getMapField()).isNull();
    }

    @Test
    void shouldBuildEmptySample() {
        // When
        Sample sample = Sample.builder().build();

        // Then
        assertThat(sample.getId()).isNull();
        assertThat(sample.getText()).isNull();
        assertThat(sample.getNumber()).isNull();
        assertThat(sample.getStatus()).isNull();
        assertThat(sample.getMapField()).isNull();
        assertThat(sample.getCreatedAt()).isNull();
        assertThat(sample.getUpdatedAt()).isNull();
    }

    @Test
    void shouldAllowBuilderMethodChaining() {
        // When
        Sample.Builder builder = Sample.builder();
        Sample.Builder chained = builder
                .id(1L)
                .text("chaining test")
                .number(50);

        // Then - Verify same builder instance is returned
        assertThat(chained).isSameAs(builder);
    }
}
