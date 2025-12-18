package internconnect.au.rw.internconnect.controller;

import internconnect.au.rw.internconnect.dto.UserResponse;
import internconnect.au.rw.internconnect.model.Role;
import internconnect.au.rw.internconnect.model.User;
import internconnect.au.rw.internconnect.repository.UserRepository;
import internconnect.au.rw.internconnect.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for authentication endpoints (registration and login).
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  private static final String EMAIL_ALREADY_IN_USE_MESSAGE = "Email already in use";
  private static final String ADMIN_REGISTRATION_FORBIDDEN_MESSAGE =
      "Admin role cannot be registered. Please contact administrator.";

  public static record RegisterRequest(String firstName, String lastName, String email,
      String password, Role role) {
  }

  public static record LoginRequest(String email, String password) {
  }

  public static record TokenResponse(String token) {
  }

  private final AuthService authService;
  private final UserRepository userRepository;

  public AuthController(AuthService authService, UserRepository userRepository) {
    this.authService = authService;
    this.userRepository = userRepository;
  }

  @PostMapping("/register")
  public UserResponse register(@RequestBody RegisterRequest request) {
    logger.info("Registration attempt for email: {}", request.email());
    if (userRepository.existsByEmail(request.email())) {
      logger.warn("Registration failed: email already in use - {}", request.email());
      throw new ResponseStatusException(HttpStatus.CONFLICT, EMAIL_ALREADY_IN_USE_MESSAGE);
    }
    if (request.role() == Role.ADMIN) {
      logger.warn("Registration failed: attempt to register as admin - {}", request.email());
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, ADMIN_REGISTRATION_FORBIDDEN_MESSAGE);
    }
    User registeredUser = authService.register(request.firstName(), request.lastName(),
        request.email(), request.password(), request.role());
    logger.info("User successfully registered: {}", request.email());
    return UserResponse.from(registeredUser);
  }

  @PostMapping("/login")
  public TokenResponse login(@RequestBody LoginRequest request) {
    logger.info("Login attempt for email: {}", request.email());
    try {
      String token = authService.login(request.email(), request.password());
      logger.info("User successfully logged in: {}", request.email());
      return new TokenResponse(token);
    } catch (AuthenticationException exception) {
      logger.warn("Login failed for email: {}", request.email());
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }
  }
}


