package com.test.api.exceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {
    public static final String TEST_OBJECT = "testObject";
    public static final String FIELD_1 = "field1";
    public static final String FIELD_IS_REQUIRED = "Field is required";
    public static final String UNCHECKED = "unchecked";
    public static final String FIELD_1_IS_REQUIRED = "Field1 is required";
    public static final String FIELD_2 = "field2";
    public static final String FIELD_2_MUST_BE_VALID = "Field2 must be valid";
    public static final String GLOBAL_VALIDATION_ERROR = "Global validation error";
    public static final String VALUE_MUST_NOT_BE_NULL = "Value must not be null";
    public static final String CONSTRAINT_VIOLATION = "Constraint violation";
    public static final String VALUE_MUST_BE_POSITIVE = "Value must be positive";
    public static final String CONSTRAINT_VIOLATIONS = "Constraint violations";
    public static final String RESOURCE_NOT_FOUND_WITH_ID_123 = "Resource not found with id: 123";
    public static final String INTERNAL_SERVER_ERROR_OCCURRED = "Internal server error occurred";
    public static final String AN_UNEXPECTED_ERROR_OCCURRED = "An unexpected error occurred";
    public static final String USER_ID = "userId";
    public static final String INVALID_ID = "invalid_id";
    public static final String INVALID_VALUE_FOR_PARAMETER_S_S = "Invalid value for parameter '%s': '%s'";
    public static final String INVALID_VALUE_FOR_PARAMETER_NULL_NULL = "Invalid value for parameter 'null': 'null'";
    public static final String ERRORS = "errors";
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;
    @Mock
    private MethodParameter methodParameter;
    @Mock
    private ConstraintViolation<Object> constraintViolation1;
    @Mock
    private ConstraintViolation<Object> constraintViolation2;
    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testGenerateNotValidException_SingleError() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, TEST_OBJECT);
        FieldError fieldError = new FieldError(TEST_OBJECT, FIELD_1, FIELD_IS_REQUIRED);
        bindingResult.addError(fieldError);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);
        ResponseEntity<?> response = globalExceptionHandler.generateNotValidException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        @SuppressWarnings(UNCHECKED)
        Map<String, List<String>> responseBody = (Map<String, List<String>>) response.getBody();
        assertTrue(responseBody.containsKey(GlobalExceptionHandler.KEY_ERRORS));
        assertEquals(1, responseBody.get(GlobalExceptionHandler.KEY_ERRORS).size());
        assertEquals(FIELD_IS_REQUIRED, responseBody.get(GlobalExceptionHandler.KEY_ERRORS).get(0));
    }

    @Test
    void testGenerateNotValidException_MultipleErrors() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, TEST_OBJECT);
        FieldError fieldError1 = new FieldError(TEST_OBJECT, FIELD_1, FIELD_1_IS_REQUIRED);
        FieldError fieldError2 = new FieldError(TEST_OBJECT, FIELD_2, FIELD_2_MUST_BE_VALID);
        ObjectError globalError = new ObjectError(TEST_OBJECT, GLOBAL_VALIDATION_ERROR);
        bindingResult.addError(fieldError1);
        bindingResult.addError(fieldError2);
        bindingResult.addError(globalError);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);
        ResponseEntity<?> response = globalExceptionHandler.generateNotValidException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        @SuppressWarnings(UNCHECKED)
        Map<String, List<String>> responseBody = (Map<String, List<String>>) response.getBody();
        assertTrue(responseBody.containsKey(GlobalExceptionHandler.KEY_ERRORS));
        assertEquals(3, responseBody.get(GlobalExceptionHandler.KEY_ERRORS).size());
        assertTrue(responseBody.get(GlobalExceptionHandler.KEY_ERRORS).contains(FIELD_1_IS_REQUIRED));
        assertTrue(responseBody.get(GlobalExceptionHandler.KEY_ERRORS).contains(FIELD_2_MUST_BE_VALID));
        assertTrue(responseBody.get(GlobalExceptionHandler.KEY_ERRORS).contains(GLOBAL_VALIDATION_ERROR));
    }

    @Test
    void testGenerateConstraintViolationException_SingleViolation() {
        when(constraintViolation1.getMessage()).thenReturn(VALUE_MUST_NOT_BE_NULL);
        Set<ConstraintViolation<Object>> violations = Set.of(constraintViolation1);
        ConstraintViolationException exception = new ConstraintViolationException(CONSTRAINT_VIOLATION, violations);
        ResponseEntity<?> response = globalExceptionHandler.generateConstraintViolationException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        @SuppressWarnings(UNCHECKED)
        Map<String, List<String>> responseBody = (Map<String, List<String>>) response.getBody();
        assertTrue(responseBody.containsKey(GlobalExceptionHandler.KEY_ERRORS));
        assertEquals(1, responseBody.get(GlobalExceptionHandler.KEY_ERRORS).size());
        assertEquals(VALUE_MUST_NOT_BE_NULL, responseBody.get(GlobalExceptionHandler.KEY_ERRORS).get(0));
    }

    @Test
    void testGenerateConstraintViolationException_MultipleViolations() {
        when(constraintViolation1.getMessage()).thenReturn(VALUE_MUST_NOT_BE_NULL);
        when(constraintViolation2.getMessage()).thenReturn(VALUE_MUST_BE_POSITIVE);
        Set<ConstraintViolation<Object>> violations = Set.of(constraintViolation1, constraintViolation2);
        ConstraintViolationException exception = new ConstraintViolationException(CONSTRAINT_VIOLATIONS, violations);
        ResponseEntity<?> response = globalExceptionHandler.generateConstraintViolationException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        @SuppressWarnings(UNCHECKED)
        Map<String, List<String>> responseBody = (Map<String, List<String>>) response.getBody();
        assertTrue(responseBody.containsKey(GlobalExceptionHandler.KEY_ERRORS));
        assertEquals(2, responseBody.get(GlobalExceptionHandler.KEY_ERRORS).size());
        assertTrue(responseBody.get(GlobalExceptionHandler.KEY_ERRORS).contains(VALUE_MUST_NOT_BE_NULL));
        assertTrue(responseBody.get(GlobalExceptionHandler.KEY_ERRORS).contains(VALUE_MUST_BE_POSITIVE));
    }

    @Test
    void testHandleEntityNotFoundException() {
        String errorMessage = RESOURCE_NOT_FOUND_WITH_ID_123;
        EntityNotFoundException exception = new EntityNotFoundException(errorMessage);
        ResponseEntity<Map<String, List<String>>> response = globalExceptionHandler.handleEntityNotFoundException(exception);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey(GlobalExceptionHandler.KEY_ERRORS));
        assertEquals(1, response.getBody().get(GlobalExceptionHandler.KEY_ERRORS).size());
        assertEquals(errorMessage, response.getBody().get(GlobalExceptionHandler.KEY_ERRORS).get(0));
    }

    @Test
    void testHandleRuntimeException() {
        String errorMessage = INTERNAL_SERVER_ERROR_OCCURRED;
        RuntimeException exception = new RuntimeException(errorMessage);
        ResponseEntity<Map<String, List<String>>> response = globalExceptionHandler.handleRuntimeException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey(GlobalExceptionHandler.KEY_ERRORS));
        assertEquals(1, response.getBody().get(GlobalExceptionHandler.KEY_ERRORS).size());
        assertEquals(errorMessage, response.getBody().get(GlobalExceptionHandler.KEY_ERRORS).get(0));
    }

    @Test
    void testHandleRuntimeException_NullMessage() {
        RuntimeException exception = new RuntimeException((String) null);
        ResponseEntity<Map<String, List<String>>> response = globalExceptionHandler.handleRuntimeException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey(GlobalExceptionHandler.KEY_ERRORS));
        assertEquals(1, response.getBody().get(GlobalExceptionHandler.KEY_ERRORS).size());
        assertEquals(AN_UNEXPECTED_ERROR_OCCURRED,response.getBody().get(GlobalExceptionHandler.KEY_ERRORS).get(0));
    }

    @Test
    void testHandleTypeMismatch() {
        String parameterName = USER_ID;
        Object invalidValue = INVALID_ID;
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn(parameterName);
        when(exception.getValue()).thenReturn(invalidValue);
        ResponseEntity<Map<String, List<String>>> response = globalExceptionHandler.handleTypeMismatch(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey(GlobalExceptionHandler.KEY_ERRORS));
        assertEquals(1, response.getBody().get(GlobalExceptionHandler.KEY_ERRORS).size());
        String expectedMessage = String.format(INVALID_VALUE_FOR_PARAMETER_S_S, parameterName, invalidValue);
        assertEquals(expectedMessage, response.getBody().get(GlobalExceptionHandler.KEY_ERRORS).get(0));
    }

    @Test
    void testHandleTypeMismatch_NullValues() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn(null);
        when(exception.getValue()).thenReturn(null);
        ResponseEntity<Map<String, List<String>>> response = globalExceptionHandler.handleTypeMismatch(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey(GlobalExceptionHandler.KEY_ERRORS));
        assertEquals(1, response.getBody().get(GlobalExceptionHandler.KEY_ERRORS).size());
        assertEquals(INVALID_VALUE_FOR_PARAMETER_NULL_NULL, response.getBody().get(GlobalExceptionHandler.KEY_ERRORS).get(0));
    }

    @Test
    void testConstantValue() {
        assertEquals(ERRORS, GlobalExceptionHandler.KEY_ERRORS);
    }
}
