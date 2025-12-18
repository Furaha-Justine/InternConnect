package internconnect.au.rw.internconnect;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import internconnect.au.rw.internconnect.config.SecurityConfig;
import internconnect.au.rw.internconnect.controller.AuthController;
import internconnect.au.rw.internconnect.service.AuthService;
import internconnect.au.rw.internconnect.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@SpringBootTest
@ActiveProfiles("test")
@TestExecutionListeners(
    listeners = {DependencyInjectionTestExecutionListener.class},
    mergeMode = TestExecutionListeners.MergeMode.REPLACE_DEFAULTS
)
@DisplayName("Application Context Tests")
class InternConnectApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired(required = false)
	private AuthController authController;

	@Autowired(required = false)
	private AuthService authService;

	@Autowired(required = false)
	private UserService userService;

	@Autowired(required = false)
	private SecurityConfig securityConfig;

	@Test
	@DisplayName("Should load Spring application context successfully")
	void contextLoads() {
		// Verify Spring context loads successfully
		assertNotNull(applicationContext, "Application context should not be null");
		assertTrue(applicationContext.getBeanDefinitionCount() > 0, 
			"Application context should have beans");
	}

	@Test
	@DisplayName("Should autowire AuthController bean")
	void shouldAutowireAuthController() {
		assertNotNull(authController, "AuthController should be autowired");
	}

	@Test
	@DisplayName("Should autowire AuthService bean")
	void shouldAutowireAuthService() {
		assertNotNull(authService, "AuthService should be autowired");
	}

	@Test
	@DisplayName("Should autowire UserService bean")
	void shouldAutowireUserService() {
		assertNotNull(userService, "UserService should be autowired");
	}

	@Test
	@DisplayName("Should autowire SecurityConfig bean")
	void shouldAutowireSecurityConfig() {
		assertNotNull(securityConfig, "SecurityConfig should be autowired");
	}
}
