package internconnect.au.rw.internconnect.service;

import internconnect.au.rw.internconnect.model.Application;
import internconnect.au.rw.internconnect.repository.ApplicationRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for application management operations.
 */
@Service
public class ApplicationService {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

  private final ApplicationRepository applicationRepository;

  public ApplicationService(ApplicationRepository applicationRepository) {
    this.applicationRepository = applicationRepository;
  }

  public Application apply(Application application) {
    logger.info("Creating new application for internship: {}", application.getInternship().getId());
    return applicationRepository.save(application);
  }

  public Page<Application> getByStudentId(UUID studentId, Pageable pageable) {
    logger.debug("Fetching applications for student ID: {}", studentId);
    return applicationRepository.findByStudentProfileId(studentId, pageable);
  }

  public Page<Application> getByInternshipId(UUID internshipId, Pageable pageable) {
    logger.debug("Fetching applications for internship ID: {}", internshipId);
    return applicationRepository.findByInternshipId(internshipId, pageable);
  }

  public Application get(UUID id) {
    logger.debug("Fetching application with ID: {}", id);
    return applicationRepository.findById(id).orElse(null);
  }

  public Page<Application> list(Pageable pageable) {
    logger.debug("Listing all applications");
    return applicationRepository.findAll(pageable);
  }

  public Application update(UUID id, Application application) {
    logger.info("Updating application with ID: {}", id);
    application.setId(id);
    return applicationRepository.save(application);
  }

  public void delete(UUID id) {
    logger.info("Deleting application with ID: {}", id);
    applicationRepository.deleteById(id);
  }

  public Page<Application> getByCompanyEmail(String email, Pageable pageable) {
    logger.debug("Fetching applications for company email: {}", email);
    return applicationRepository.findByCompanyUserEmail(email, pageable);
  }

  public boolean hasAlreadyApplied(UUID studentProfileId, UUID internshipId) {
    logger.debug("Checking if student {} already applied to internship {}", studentProfileId,
        internshipId);
    return applicationRepository.existsByStudentProfileIdAndInternshipId(studentProfileId,
        internshipId);
  }
}
