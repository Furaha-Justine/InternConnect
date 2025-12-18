package internconnect.au.rw.internconnect.controller;

import internconnect.au.rw.internconnect.dto.UserResponse;
import internconnect.au.rw.internconnect.model.Role;
import internconnect.au.rw.internconnect.model.User;
import internconnect.au.rw.internconnect.service.SecurityService;
import internconnect.au.rw.internconnect.service.UserService;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for user management endpoints.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);
  private static final String USER_NOT_FOUND_MESSAGE = "User not found";
  private static final String ACCESS_DENIED_MESSAGE = "Access denied";
  private static final String ROLE_CHANGE_FORBIDDEN_MESSAGE = "Cannot change role";

  private final UserService userService;
  private final SecurityService securityService;

  public UserController(UserService userService, SecurityService securityService) {
    this.userService = userService;
    this.securityService = securityService;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public Page<UserResponse> list(Pageable pageable) {
    logger.debug("Listing all users");
    return userService.listAll(pageable).map(UserResponse::from);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public UserResponse create(@RequestBody User user) {
    logger.info("Creating new user: {}", user.getEmail());
    User createdUser = userService.create(user);
    return UserResponse.from(createdUser);
  }

  @GetMapping("/{id}")
  public UserResponse get(@PathVariable UUID id) {
    logger.debug("Fetching user with ID: {}", id);
    User currentUser = securityService.getCurrentUser(userService);
    if (currentUser == null) {
      logger.warn("Unauthorized access attempt to user: {}", id);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, USER_NOT_FOUND_MESSAGE);
    }
    if (!securityService.canAccessResource(id, userService)) {
      logger.warn("Access denied for user ID: {} by user: {}", id, currentUser.getEmail());
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, ACCESS_DENIED_MESSAGE);
    }
    User user = userService.get(id);
    return UserResponse.from(user);
  }

  @PutMapping("/{id}")
  public UserResponse update(@PathVariable UUID id, @RequestBody User user) {
    logger.info("Updating user with ID: {}", id);
    User currentUser = securityService.getCurrentUser(userService);
    if (currentUser == null) {
      logger.warn("Unauthorized update attempt for user: {}", id);
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, USER_NOT_FOUND_MESSAGE);
    }
    if (!securityService.canAccessResource(id, userService)) {
      logger.warn("Update denied for user ID: {} by user: {}", id, currentUser.getEmail());
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, ACCESS_DENIED_MESSAGE);
    }
    if (!securityService.isAdmin() && user.getRole() != null
        && !user.getRole().equals(currentUser.getRole())) {
      logger.warn("Role change attempt denied for user ID: {}", id);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, ROLE_CHANGE_FORBIDDEN_MESSAGE);
    }
    User updatedUser = userService.update(id, user);
    return UserResponse.from(updatedUser);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public void delete(@PathVariable UUID id) {
    logger.info("Deleting user with ID: {}", id);
    userService.delete(id);
  }

  @GetMapping("/by-province-name")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public Page<UserResponse> findByProvinceName(@RequestParam String name, Pageable pageable) {
    logger.debug("Finding users by province name: {}", name);
    return userService.findByProvinceName(name, pageable).map(UserResponse::from);
  }

  @GetMapping("/by-province-code")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public Page<UserResponse> findByProvinceCode(@RequestParam String code, Pageable pageable) {
    logger.debug("Finding users by province code: {}", code);
    return userService.findByProvinceCode(code, pageable).map(UserResponse::from);
  }

  @GetMapping("/by-role")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public Page<UserResponse> findByRole(@RequestParam Role role, Pageable pageable) {
    logger.debug("Finding users by role: {}", role);
    return userService.findByRole(role, pageable).map(UserResponse::from);
  }

  @GetMapping("/{id}/location")
  public Map<String, Object> getUserLocation(@PathVariable UUID id) {
    logger.debug("Fetching location for user ID: {}", id);
    User currentUser = securityService.getCurrentUser(userService);
    if (currentUser == null || !id.equals(currentUser.getId())) {
      logger.warn("Access denied for location of user ID: {}", id);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, ACCESS_DENIED_MESSAGE);
    }
    return userService.loadLocationFor(currentUser);
  }
}
