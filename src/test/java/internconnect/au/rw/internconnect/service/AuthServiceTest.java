package internconnect.au.rw.internconnect.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import internconnect.au.rw.internconnect.model.Role;
import internconnect.au.rw.internconnect.model.User;
import internconnect.au.rw.internconnect.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Unit tests for AuthService.
 * Tests business logic in isolation using mocks.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtService jwtService;

  @InjectMocks
  private AuthService authService;

  private User testUser;
  private String testEmail;
  private String testPassword;
  private String encodedPassword;

  @BeforeEach
  void setUp() {
    testEmail = "test@example.com";
    testPassword = "password123";
    encodedPassword = "$2a$10$encodedPasswordHash";

    testUser = new User();
    testUser.setId(UUID.randomUUID());
    testUser.setFirstName("John");
    testUser.setLastName("Doe");
    testUser.setEmail(testEmail);
    testUser.setPassword(encodedPassword);
    testUser.setRole(Role.STUDENT);
  }

  @Test
  @DisplayName("Should register new user successfully")
  void shouldRegisterNewUserSuccessfully() {
    // Given
    when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // When
    User result = authService.register("John", "Doe", testEmail, testPassword, Role.STUDENT);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo(testEmail);
    assertThat(result.getFirstName()).isEqualTo("John");
    assertThat(result.getLastName()).isEqualTo("Doe");
    assertThat(result.getRole()).isEqualTo(Role.STUDENT);
    assertThat(result.getPassword()).isEqualTo(encodedPassword);

    // Verify interactions
    verify(passwordEncoder).encode(testPassword);
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("Should encode password when registering user")
  void shouldEncodePasswordWhenRegistering() {
    // Given
    when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // When
    authService.register("John", "Doe", testEmail, testPassword, Role.STUDENT);

    // Then
    verify(passwordEncoder).encode(testPassword);
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("Should login user successfully and return JWT token")
  void shouldLoginUserSuccessfullyAndReturnToken() {
    // Given
    String expectedToken = "jwt.token.here";
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(null);
    when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
    when(jwtService.generateToken(any(UUID.class), anyString(), anyString()))
        .thenReturn(expectedToken);

    // When
    String token = authService.login(testEmail, testPassword);

    // Then
    assertThat(token).isNotNull();
    assertThat(token).isEqualTo(expectedToken);

    // Verify interactions
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userRepository).findByEmail(testEmail);
    verify(jwtService).generateToken(testUser.getId(), testEmail, Role.STUDENT.name());
  }

  @Test
  @DisplayName("Should throw exception when login authentication fails")
  void shouldThrowExceptionWhenLoginAuthenticationFails() {
    // Given
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException("Bad credentials"));

    // When/Then
    assertThatThrownBy(() -> authService.login(testEmail, "wrongPassword"))
        .isInstanceOf(BadCredentialsException.class)
        .hasMessageContaining("Bad credentials");

    // Verify user repository was not called
    verify(userRepository, never()).findByEmail(anyString());
    verify(jwtService, never()).generateToken(any(), anyString(), anyString());
  }

  @Test
  @DisplayName("Should throw exception when user not found after authentication")
  void shouldThrowExceptionWhenUserNotFoundAfterAuthentication() {
    // Given
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(null);
    when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

    // When/Then
    assertThatThrownBy(() -> authService.login(testEmail, testPassword))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("User not found after authentication");

    // Verify interactions
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(userRepository).findByEmail(testEmail);
    verify(jwtService, never()).generateToken(any(), anyString(), anyString());
  }

  @Test
  @DisplayName("Should register user with COMPANY role")
  void shouldRegisterUserWithCompanyRole() {
    // Given
    when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
    testUser.setRole(Role.COMPANY);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // When
    User result = authService.register("Jane", "Smith", testEmail, testPassword, Role.COMPANY);

    // Then
    assertThat(result.getRole()).isEqualTo(Role.COMPANY);
    verify(userRepository).save(any(User.class));
  }
}

