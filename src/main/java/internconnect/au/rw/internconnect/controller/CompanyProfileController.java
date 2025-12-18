package internconnect.au.rw.internconnect.controller;

import internconnect.au.rw.internconnect.model.CompanyProfile;
import internconnect.au.rw.internconnect.model.User;
import internconnect.au.rw.internconnect.service.CompanyProfileService;
import internconnect.au.rw.internconnect.service.SecurityService;
import internconnect.au.rw.internconnect.service.UserService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for company profile management endpoints.
 */
@RestController
@RequestMapping("/api/company-profiles")
@CrossOrigin
public class CompanyProfileController {

  private static final Logger logger = LoggerFactory.getLogger(CompanyProfileController.class);

  private final CompanyProfileService companyProfileService;
  private final UserService userService;
  private final SecurityService securityService;

  public CompanyProfileController(CompanyProfileService companyProfileService,
      UserService userService, SecurityService securityService) {
    this.companyProfileService = companyProfileService;
    this.userService = userService;
    this.securityService = securityService;
  }

  @GetMapping
  public Page<CompanyProfile> list(Pageable pageable) {
    logger.debug("Listing all company profiles");
    return companyProfileService.list(pageable);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_COMPANY')")
  public CompanyProfile create(@RequestBody CompanyProfile companyProfile) {
    logger.info("Creating new company profile");
    String email = securityService.getCurrentUserEmail();
    User currentUser = userService.findByEmail(email);
    companyProfile.setUser(currentUser);
    return companyProfileService.create(companyProfile);
  }

  @GetMapping("/{id}")
  public CompanyProfile get(@PathVariable UUID id) {
    logger.debug("Fetching company profile with ID: {}", id);
    return companyProfileService.get(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_COMPANY')")
  public CompanyProfile update(@PathVariable UUID id, @RequestBody CompanyProfile companyProfile) {
    logger.info("Updating company profile with ID: {}", id);
    return companyProfileService.update(id, companyProfile);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ROLE_COMPANY')")
  public void delete(@PathVariable UUID id) {
    logger.info("Deleting company profile with ID: {}", id);
    companyProfileService.delete(id);
  }
}


