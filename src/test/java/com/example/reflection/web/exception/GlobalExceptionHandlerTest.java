package com.example.reflection.web.exception;

import com.example.reflection.domain.exception.SampleNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GlobalExceptionHandler.
 * Tests all exception handling scenarios and error response formatting.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void shouldHandleValidationExceptionWithFieldErrors() throws NoSuchMethodException {
        // Given
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "testObject");
        bindingResult.addError(new FieldError("testObject", "text", "must not be blank"));
        bindingResult.addError(new FieldError("testObject", "number", "must be positive"));

        MethodParameter parameter = new MethodParameter(
                this.getClass().getDeclaredMethod("shouldHandleValidationExceptionWithFieldErrors"), -1);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Validation failed");
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().timestamp()).isNotNull();
        assertThat(response.getBody().fieldErrors()).hasSize(2);
        assertThat(response.getBody().fieldErrors())
                .extracting(ErrorResponse.FieldError::field)
                .containsExactlyInAnyOrder("text", "number");
        assertThat(response.getBody().fieldErrors())
                .extracting(ErrorResponse.FieldError::message)
                .containsExactlyInAnyOrder("must not be blank", "must be positive");
    }

    @Test
    void shouldHandleValidationExceptionWithSingleFieldError() throws NoSuchMethodException {
        // Given
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "testObject");
        bindingResult.addError(new FieldError("testObject", "email", "must be a valid email"));

        MethodParameter parameter = new MethodParameter(
                this.getClass().getDeclaredMethod("shouldHandleValidationExceptionWithSingleFieldError"), -1);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().fieldErrors()).hasSize(1);
        assertThat(response.getBody().fieldErrors().get(0).field()).isEqualTo("email");
        assertThat(response.getBody().fieldErrors().get(0).message()).isEqualTo("must be a valid email");
    }

    @Test
    void shouldHandleJsonParseExceptionForInvalidEnumValue() {
        // Given
        String errorMessage = "JSON parse error: Cannot deserialize value of type `Status` from String \"INVALID\": " +
                "not one of the values accepted for Enum class: [ACTIVE, INACTIVE]";
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleJsonParseException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Invalid enum value provided");
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().timestamp()).isNotNull();
        assertThat(response.getBody().fieldErrors()).isNull();
    }

    @Test
    void shouldHandleJsonParseExceptionForGenericParseError() {
        // Given
        String errorMessage = "Malformed JSON request";
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleJsonParseException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Invalid request format");
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void shouldHandleJsonParseExceptionWithNullMessage() {
        // Given
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException(
                null, (Throwable) null);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleJsonParseException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Invalid request format");
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    void shouldHandleSampleNotFoundException() {
        // Given
        Long sampleId = 999L;
        SampleNotFoundException exception = new SampleNotFoundException("Sample not found with id: " + sampleId);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleSampleNotFound(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Sample not found with id: 999");
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().timestamp()).isNotNull();
        assertThat(response.getBody().fieldErrors()).isNull();
    }

    @Test
    void shouldHandleGenericExceptionWithMessage() {
        // Given
        Exception exception = new RuntimeException("Something went wrong");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Something went wrong");
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().timestamp()).isNotNull();
        assertThat(response.getBody().fieldErrors()).isNull();
    }

    @Test
    void shouldHandleGenericExceptionWithNullMessage() {
        // Given
        Exception exception = new RuntimeException((String) null);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void shouldHandleNullPointerException() {
        // Given
        Exception exception = new NullPointerException("Null value encountered");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Null value encountered");
        assertThat(response.getBody().status()).isEqualTo(500);
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        // Given
        Exception exception = new IllegalArgumentException("Invalid argument provided");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Invalid argument provided");
        assertThat(response.getBody().status()).isEqualTo(500);
    }

    @Test
    void shouldIncludeTimestampInAllResponses() throws NoSuchMethodException {
        // Given - Create different exception types
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "test");
        bindingResult.addError(new FieldError("test", "field", "error"));
        MethodParameter parameter = new MethodParameter(
                this.getClass().getDeclaredMethod("shouldIncludeTimestampInAllResponses"), -1);
        MethodArgumentNotValidException validationException = new MethodArgumentNotValidException(parameter, bindingResult);
        HttpMessageNotReadableException parseException = new HttpMessageNotReadableException("parse error", (Throwable) null);
        SampleNotFoundException notFoundException = new SampleNotFoundException("Sample not found with id: 1");
        Exception genericException = new Exception("generic error");

        // When - Handle all exception types
        ResponseEntity<ErrorResponse> validationResponse = exceptionHandler.handleValidationException(validationException);
        ResponseEntity<ErrorResponse> parseResponse = exceptionHandler.handleJsonParseException(parseException);
        ResponseEntity<ErrorResponse> notFoundResponse = exceptionHandler.handleSampleNotFound(notFoundException);
        ResponseEntity<ErrorResponse> genericResponse = exceptionHandler.handleGenericException(genericException);

        // Then - All should have timestamps
        assertThat(validationResponse.getBody().timestamp()).isNotNull().isNotEmpty();
        assertThat(parseResponse.getBody().timestamp()).isNotNull().isNotEmpty();
        assertThat(notFoundResponse.getBody().timestamp()).isNotNull().isNotEmpty();
        assertThat(genericResponse.getBody().timestamp()).isNotNull().isNotEmpty();
    }
}
