package internconnect.au.rw.internconnect.config;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleRse(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", ex.getStatusCode().value(),
                "error", ex.getReason() == null ? ex.getStatusCode().toString() : ex.getReason()
        ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuth(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 401,
                "error", "Invalid email or password"
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 400,
                "error", "Validation failed"
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 500,
                "error", "Unexpected error"
        ));
    }
}


