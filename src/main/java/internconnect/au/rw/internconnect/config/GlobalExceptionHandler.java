package internconnect.au.rw.internconnect.config;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * Global exception handler for the application.
 * Handles all exceptions and returns consistent error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private static final String TIMESTAMP_KEY = "timestamp";
  private static final String STATUS_KEY = "status";
  private static final String ERROR_KEY = "error";
  private static final String MESSAGE_KEY = "message";
  private static final String VALIDATION_ERRORS_KEY = "validationErrors";

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatusException(
      ResponseStatusException exception) {
    logger.warn("ResponseStatusException: {} - {}", exception.getStatusCode(),
        exception.getReason());
    Map<String, Object> response = new HashMap<>();
    response.put(TIMESTAMP_KEY, Instant.now().toString());
    response.put(STATUS_KEY, exception.getStatusCode().value());
    response.put(ERROR_KEY,
        exception.getReason() != null ? exception.getReason()
            : exception.getStatusCode().toString());
    return ResponseEntity.status(exception.getStatusCode()).body(response);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, Object>> handleAuthenticationException(
      AuthenticationException exception) {
    logger.warn("Authentication failed: {}", exception.getMessage());
    Map<String, Object> response = new HashMap<>();
    response.put(TIMESTAMP_KEY, Instant.now().toString());
    response.put(STATUS_KEY, HttpStatus.UNAUTHORIZED.value());
    response.put(ERROR_KEY, "Authentication failed");
    response.put(MESSAGE_KEY, "Invalid email or password");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(
      MethodArgumentNotValidException exception) {
    logger.warn("Validation failed: {}", exception.getMessage());
    Map<String, String> validationErrors = exception.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
            (existing, replacement) -> existing));
    Map<String, Object> response = new HashMap<>();
    response.put(TIMESTAMP_KEY, Instant.now().toString());
    response.put(STATUS_KEY, HttpStatus.BAD_REQUEST.value());
    response.put(ERROR_KEY, "Validation failed");
    response.put(VALIDATION_ERRORS_KEY, validationErrors);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception exception) {
    logger.error("Unexpected error occurred", exception);
    Map<String, Object> response = new HashMap<>();
    response.put(TIMESTAMP_KEY, Instant.now().toString());
    response.put(STATUS_KEY, HttpStatus.INTERNAL_SERVER_ERROR.value());
    response.put(ERROR_KEY, "An unexpected error occurred");
    response.put(MESSAGE_KEY, "Please contact support if the problem persists");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}


