package internconnect.au.rw.internconnect.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import internconnect.au.rw.internconnect.model.Role;
import internconnect.au.rw.internconnect.model.User;
import internconnect.au.rw.internconnect.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for AuthService.
 * Uses real Spring context and H2 database (no mocks).
 */
@SpringBootTest
@ActiveProfiles("test")
@TestExecutionListeners(
    listeners = {DependencyInjectionTestExecutionListener.class},
    mergeMode = TestExecutionListeners.MergeMode.REPLACE_DEFAULTS
)
@DisplayName("AuthService Integration Tests")
class AuthServiceIntegrationTest {

  @Autowired
  private AuthService authService;

  @Autowired
  private UserRepository userRepository;

  private User createdUser;

  @AfterEach
  void tearDown() {
    if (createdUser != null) {
      userRepository.deleteById(createdUser.getId());
      createdUser = null;
    }
  }

  @Test
  @DisplayName("Should register new user successfully with real database")
  @Transactional
  void shouldRegisterNewUserSuccessfully() {
    // Given
    String firstName = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";
    String password = "password123";
    Role role = Role.STUDENT;

    // When
    User result = authService.register(firstName, lastName, email, password, role);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getEmail()).isEqualTo(email);
    assertThat(result.getFirstName()).isEqualTo(firstName);
    assertThat(result.getLastName()).isEqualTo(lastName);
    assertThat(result.getRole()).isEqualTo(role);
    assertThat(result.getPassword()).isNotEqualTo(password); // Should be encoded

    // Verify user is saved in database
    assertThat(userRepository.findById(result.getId())).isPresent();
    createdUser = result;
  }

  @Test
  @DisplayName("Should login user successfully with real authentication")
  @Transactional
  void shouldLoginUserSuccessfully() {
    // Given - register a user first
    String email = "test.login@example.com";
    String password = "password123";
    User registeredUser = authService.register("Test", "User", email, password, Role.STUDENT);
    createdUser = registeredUser;

    // When
    String token = authService.login(email, password);

    // Then
    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
  }

  @Test
  @DisplayName("Should throw exception for invalid login credentials")
  @Transactional
  void shouldThrowExceptionForInvalidCredentials() {
    // Given - register a user
    String email = "test.invalid@example.com";
    String password = "correctPassword";
    User registeredUser = authService.register("Test", "User", email, password, Role.STUDENT);
    createdUser = registeredUser;

    // When / Then
    assertThrows(BadCredentialsException.class, () -> {
      authService.login(email, "wrongPassword");
    });
  }

  @Test
  @DisplayName("Should encode password during registration")
  @Transactional
  void shouldEncodePasswordDuringRegistration() {
    // Given
    String password = "plainPassword123";

    // When
    User user = authService.register("Test", "User", "encode@test.com", password, Role.STUDENT);
    createdUser = user;

    // Then
    assertThat(user.getPassword()).isNotEqualTo(password);
    assertThat(user.getPassword()).isNotNull();
    assertThat(user.getPassword().length()).isGreaterThan(20); // BCrypt hash length
  }

  @Test
  @DisplayName("Should verify AuthService bean is autowired")
  void shouldVerifyAuthServiceBeanIsAutowired() {
    // Then
    assertNotNull(authService, "AuthService should be autowired");
    assertNotNull(userRepository, "UserRepository should be autowired");
  }
}

