package internconnect.au.rw.internconnect.dto;

import internconnect.au.rw.internconnect.model.Role;
import internconnect.au.rw.internconnect.model.User;
import java.util.UUID;

/**
 * Data Transfer Object for User responses.
 * Exposes only safe, non-sensitive user information.
 */
public record UserResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    String phone,
    Role role
) {
  /**
   * Creates a UserResponse from a User entity.
   * Excludes sensitive fields like password and relationships.
   *
   * @param user the user entity
   * @return UserResponse DTO
   */
  public static UserResponse from(User user) {
    if (user == null) {
      return null;
    }
    return new UserResponse(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getPhone(),
        user.getRole()
    );
  }
}

