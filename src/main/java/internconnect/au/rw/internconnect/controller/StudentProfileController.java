package internconnect.au.rw.internconnect.controller;

import internconnect.au.rw.internconnect.model.StudentProfile;
import internconnect.au.rw.internconnect.model.User;
import internconnect.au.rw.internconnect.service.SecurityService;
import internconnect.au.rw.internconnect.service.StudentProfileService;
import internconnect.au.rw.internconnect.service.UserService;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

/**
 * Controller for student profile management endpoints.
 */
@RestController
@RequestMapping("/api/student-profiles")
@CrossOrigin
public class StudentProfileController {

  private static final Logger logger = LoggerFactory.getLogger(StudentProfileController.class);

  private final StudentProfileService studentProfileService;
  private final UserService userService;
  private final SecurityService securityService;

  public StudentProfileController(StudentProfileService studentProfileService,
      UserService userService, SecurityService securityService) {
    this.studentProfileService = studentProfileService;
    this.userService = userService;
    this.securityService = securityService;
  }

  @GetMapping
  public Page<StudentProfile> list(@RequestParam(required = false) String query,
      Pageable pageable) {
    logger.debug("Listing student profiles with query: {}", query);
    return studentProfileService.list(query, pageable);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_STUDENT')")
  public StudentProfile create(@RequestBody StudentProfile studentProfile) {
    logger.info("Creating new student profile");
    String email = securityService.getCurrentUserEmail();
    User currentUser = userService.findByEmail(email);
    studentProfile.setUser(currentUser);
    return studentProfileService.create(studentProfile);
  }

  @GetMapping("/{id}")
  public StudentProfile get(@PathVariable UUID id) {
    logger.debug("Fetching student profile with ID: {}", id);
    return studentProfileService.get(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_STUDENT')")
  public StudentProfile update(@PathVariable UUID id,
      @RequestBody StudentProfile studentProfile) {
    logger.info("Updating student profile with ID: {}", id);
    return studentProfileService.update(id, studentProfile);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_STUDENT')")
  public void delete(@PathVariable UUID id) {
    logger.info("Deleting student profile with ID: {}", id);
    studentProfileService.delete(id);
  }

  @PostMapping("/{id}/skills")
  @PreAuthorize("hasAuthority('ROLE_STUDENT')")
  public StudentProfile addSkills(@PathVariable UUID id, @RequestBody List<UUID> skillIds) {
    logger.info("Adding skills to student profile with ID: {}", id);
    return studentProfileService.addSkills(id, skillIds);
  }
}


