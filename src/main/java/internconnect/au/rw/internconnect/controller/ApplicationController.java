package internconnect.au.rw.internconnect.controller;

import internconnect.au.rw.internconnect.model.Application;
import internconnect.au.rw.internconnect.model.Internship;
import internconnect.au.rw.internconnect.service.ApplicationService;
import internconnect.au.rw.internconnect.service.InternshipService;
import internconnect.au.rw.internconnect.service.SecurityService;
import internconnect.au.rw.internconnect.service.StudentProfileService;
import java.time.Instant;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for application management endpoints.
 */
@RestController
@RequestMapping("/api/applications")
@CrossOrigin
public class ApplicationController {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);
  private static final String PROFILE_REQUIRED_MESSAGE = "Create your student profile first";
  private static final String OWN_PROFILE_REQUIRED_MESSAGE =
      "You can only apply with your own profile";
  private static final String INTERNSHIP_ID_REQUIRED_MESSAGE = "Internship id is required";
  private static final String ALREADY_APPLIED_MESSAGE =
      "You already applied to this internship";
  private static final String ACCESS_DENIED_MESSAGE = "Access denied";
  private static final String APPLICATION_NOT_FOUND_MESSAGE = "Application not found";

  private final ApplicationService applicationService;
  private final StudentProfileService studentProfileService;
  private final InternshipService internshipService;
  private final SecurityService securityService;

  public ApplicationController(ApplicationService applicationService,
      StudentProfileService studentProfileService, InternshipService internshipService,
      SecurityService securityService) {
    this.applicationService = applicationService;
    this.studentProfileService = studentProfileService;
    this.internshipService = internshipService;
    this.securityService = securityService;
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_STUDENT')")
  public Application apply(@RequestBody Application application) {
    logger.info("New application submission");
    String email = securityService.getCurrentUserEmail();
    var studentProfile = studentProfileService.findByUserEmail(email);
    if (studentProfile == null) {
      logger.warn("Application rejected: student profile not found for {}", email);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, PROFILE_REQUIRED_MESSAGE);
    }
    if (application.getStudentProfile() != null
        && application.getStudentProfile().getId() != null
        && !studentProfile.getId().equals(application.getStudentProfile().getId())) {
      logger.warn("Application rejected: profile mismatch for {}", email);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, OWN_PROFILE_REQUIRED_MESSAGE);
    }
    if (application.getInternship() == null || application.getInternship().getId() == null) {
      logger.warn("Application rejected: missing internship ID");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INTERNSHIP_ID_REQUIRED_MESSAGE);
    }
    UUID internshipId = application.getInternship().getId();
    if (applicationService.hasAlreadyApplied(studentProfile.getId(), internshipId)) {
      logger.warn("Application rejected: already applied to internship {}", internshipId);
      throw new ResponseStatusException(HttpStatus.CONFLICT, ALREADY_APPLIED_MESSAGE);
    }
    application.setStudentProfile(studentProfile);
    application.setAppliedAt(Instant.now());
    logger.info("Application created successfully for internship: {}", internshipId);
    return applicationService.apply(application);
  }

  @GetMapping
  public Page<Application> list(Pageable pageable) {
    if (securityService.isStudent()) {
      String email = securityService.getCurrentUserEmail();
      logger.debug("Listing applications for student: {}", email);
      var profile = studentProfileService.findByUserEmail(email);
      if (profile != null) {
        return applicationService.getByStudentId(profile.getId(), pageable);
      }
      return Page.empty(pageable);
    }
    if (securityService.isCompany()) {
      String email = securityService.getCurrentUserEmail();
      logger.debug("Listing applications for company: {}", email);
      return applicationService.getByCompanyEmail(email, pageable);
    }
    logger.debug("Listing all applications");
    return applicationService.list(pageable);
  }

  @GetMapping("/student/{studentId}")
  public Page<Application> getByStudentId(@PathVariable UUID studentId, Pageable pageable) {
    logger.debug("Fetching applications for student ID: {}", studentId);
    return applicationService.getByStudentId(studentId, pageable);
  }

  @GetMapping("/internship/{internshipId}")
  @PreAuthorize("hasAuthority('ROLE_COMPANY')")
  public Page<Application> getByInternshipId(@PathVariable UUID internshipId,
      Pageable pageable) {
    logger.debug("Fetching applications for internship ID: {}", internshipId);
    String email = securityService.getCurrentUserEmail();
    Internship internship = internshipService.get(internshipId);
    if (internship == null || internship.getCompanyProfile() == null
        || internship.getCompanyProfile().getUser() == null
        || !email.equals(internship.getCompanyProfile().getUser().getEmail())) {
      logger.warn("Access denied to internship applications: {}", internshipId);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, ACCESS_DENIED_MESSAGE);
    }
    return applicationService.getByInternshipId(internshipId, pageable);
  }

  @GetMapping("/{id}")
  public Application get(@PathVariable UUID id) {
    logger.debug("Fetching application with ID: {}", id);
    return applicationService.get(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_COMPANY')")
  public Application update(@PathVariable UUID id, @RequestBody Application application) {
    logger.info("Updating application with ID: {}", id);
    String email = securityService.getCurrentUserEmail();
    Application existingApplication = applicationService.get(id);
    if (existingApplication == null) {
      logger.warn("Application not found: {}", id);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, APPLICATION_NOT_FOUND_MESSAGE);
    }
    Internship internship = existingApplication.getInternship();
    if (internship == null || internship.getCompanyProfile() == null
        || internship.getCompanyProfile().getUser() == null
        || !email.equals(internship.getCompanyProfile().getUser().getEmail())) {
      logger.warn("Update denied for application: {}", id);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, ACCESS_DENIED_MESSAGE);
    }
    existingApplication.setStatus(application.getStatus());
    return applicationService.update(id, existingApplication);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable UUID id) {
    logger.info("Deleting application with ID: {}", id);
    applicationService.delete(id);
  }

  @GetMapping("/me")
  @PreAuthorize("hasAuthority('ROLE_STUDENT')")
  public Page<Application> getMyApplications(Pageable pageable) {
    String email = securityService.getCurrentUserEmail();
    logger.debug("Fetching applications for student: {}", email);
    var profile = studentProfileService.findByUserEmail(email);
    if (profile == null) {
      return Page.empty(pageable);
    }
    return applicationService.getByStudentId(profile.getId(), pageable);
  }

  @GetMapping("/company")
  @PreAuthorize("hasAuthority('ROLE_COMPANY')")
  public Page<Application> getApplicationsForMyCompany(Pageable pageable) {
    String email = securityService.getCurrentUserEmail();
    logger.debug("Fetching applications for company: {}", email);
    return applicationService.getByCompanyEmail(email, pageable);
  }
}
