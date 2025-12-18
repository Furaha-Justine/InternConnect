package internconnect.au.rw.internconnect.controller;

import internconnect.au.rw.internconnect.model.Internship;
import internconnect.au.rw.internconnect.service.InternshipService;
import internconnect.au.rw.internconnect.service.SecurityService;
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
 * Controller for internship management endpoints.
 */
@RestController
@RequestMapping("/api/internships")
@CrossOrigin
public class InternshipController {

  private static final Logger logger = LoggerFactory.getLogger(InternshipController.class);

  private final InternshipService internshipService;
  private final SecurityService securityService;

  public InternshipController(InternshipService internshipService,
      SecurityService securityService) {
    this.internshipService = internshipService;
    this.securityService = securityService;
  }

  @GetMapping
  public Page<Internship> list(@RequestParam(required = false) String query,
      Pageable pageable) {
    if (securityService.isCompany()) {
      String email = securityService.getCurrentUserEmail();
      logger.debug("Listing internships for company user: {}", email);
      return internshipService.getByCompanyEmail(email, pageable);
    }
    logger.debug("Searching internships with query: {}", query);
    return internshipService.search(query, pageable);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_COMPANY')")
  public Internship create(@RequestBody Internship internship) {
    logger.info("Creating new internship");
    return internshipService.create(internship);
  }

  @GetMapping("/{id}")
  public Internship get(@PathVariable UUID id) {
    logger.debug("Fetching internship with ID: {}", id);
    return internshipService.get(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_COMPANY')")
  public Internship update(@PathVariable UUID id, @RequestBody Internship internship) {
    logger.info("Updating internship with ID: {}", id);
    return internshipService.update(id, internship);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_COMPANY')")
  public void delete(@PathVariable UUID id) {
    logger.info("Deleting internship with ID: {}", id);
    internshipService.delete(id);
  }

  @GetMapping("/mine")
  @PreAuthorize("hasAuthority('ROLE_COMPANY')")
  public Page<Internship> getMyInternships(Pageable pageable) {
    String email = securityService.getCurrentUserEmail();
    logger.debug("Fetching internships for company user: {}", email);
    return internshipService.getByCompanyEmail(email, pageable);
  }
}
