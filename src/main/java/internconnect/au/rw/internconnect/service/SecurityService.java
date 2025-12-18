package internconnect.au.rw.internconnect.service;

import internconnect.au.rw.internconnect.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service for security-related operations and authorization checks.
 */
@Service
public class SecurityService {

  private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);
  private static final String ROLE_ADMIN = "ROLE_ADMIN";
  private static final String ROLE_COMPANY = "ROLE_COMPANY";
  private static final String ROLE_STUDENT = "ROLE_STUDENT";

  /**
   * Gets the email of the currently authenticated user.
   *
   * @return email of the current user
   */
  public String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      logger.debug("No authenticated user found");
      return null;
    }
    String email = authentication.getName();
    logger.debug("Current user email: {}", email);
    return email;
  }

  /**
   * Gets the currently authenticated user.
   *
   * @param userService user service to fetch user details
   * @return the current user, or null if not authenticated
   */
  public User getCurrentUser(UserService userService) {
    String email = getCurrentUserEmail();
    if (email == null) {
      return null;
    }
    return userService.findByEmail(email);
  }

  /**
   * Checks if the current user has admin role.
   *
   * @return true if user is admin, false otherwise
   */
  public boolean isAdmin() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return false;
    }
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(ROLE_ADMIN::equals);
  }

  /**
   * Checks if the current user has company role.
   *
   * @return true if user is company, false otherwise
   */
  public boolean isCompany() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return false;
    }
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(ROLE_COMPANY::equals);
  }

  /**
   * Checks if the current user has student role.
   *
   * @return true if user is student, false otherwise
   */
  public boolean isStudent() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return false;
    }
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .anyMatch(ROLE_STUDENT::equals);
  }

  /**
   * Checks if the current user can access the resource (either admin or owner).
   *
   * @param resourceOwnerId the ID of the resource owner
   * @param userService user service to fetch user details
   * @return true if user can access, false otherwise
   */
  public boolean canAccessResource(java.util.UUID resourceOwnerId, UserService userService) {
    if (isAdmin()) {
      return true;
    }
    User currentUser = getCurrentUser(userService);
    if (currentUser == null) {
      return false;
    }
    return resourceOwnerId.equals(currentUser.getId());
  }
}

