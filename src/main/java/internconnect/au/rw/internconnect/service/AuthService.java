package internconnect.au.rw.internconnect.service;

import internconnect.au.rw.internconnect.model.Role;
import internconnect.au.rw.internconnect.model.User;
import internconnect.au.rw.internconnect.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for handling authentication operations (registration and login).
 */
@Service
public class AuthService {

  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  /**
   * Registers a new user in the system.
   *
   * @param firstName user's first name
   * @param lastName user's last name
   * @param email user's email address
   * @param rawPassword user's plain text password (will be encoded)
   * @param role user's role
   * @return the created user
   */
  public User register(String firstName, String lastName, String email, String rawPassword,
      Role role) {
    logger.debug("Registering new user with email: {}", email);
    User user = new User();
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setEmail(email);
    user.setRole(role);
    user.setPassword(passwordEncoder.encode(rawPassword));
    User savedUser = userRepository.save(user);
    logger.debug("User registered successfully with ID: {}", savedUser.getId());
    return savedUser;
  }

  /**
   * Authenticates a user and generates a JWT token.
   *
   * @param email user's email address
   * @param rawPassword user's plain text password
   * @return JWT token string
   * @throws org.springframework.security.core.AuthenticationException if authentication fails
   */
  public String login(String email, String rawPassword) {
    logger.debug("Attempting to authenticate user: {}", email);
    authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(email, rawPassword));
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalStateException("User not found after authentication"));
    String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
    logger.debug("JWT token generated successfully for user: {}", email);
    return token;
  }
}


