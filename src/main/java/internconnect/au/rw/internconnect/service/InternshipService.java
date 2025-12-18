package internconnect.au.rw.internconnect.service;

import internconnect.au.rw.internconnect.model.Internship;
import internconnect.au.rw.internconnect.repository.InternshipRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for internship management operations.
 */
@Service
public class InternshipService {

  private static final Logger logger = LoggerFactory.getLogger(InternshipService.class);

  private final InternshipRepository internshipRepository;

  public InternshipService(InternshipRepository internshipRepository) {
    this.internshipRepository = internshipRepository;
  }

  /**
   * Searches for internships by title or returns all if query is empty.
   *
   * @param query search query string
   * @param pageable pagination parameters
   * @return page of internships
   */
  public Page<Internship> search(String query, Pageable pageable) {
    if (query == null || query.isBlank()) {
      logger.debug("Searching all internships");
      return internshipRepository.findAll(pageable);
    }
    logger.debug("Searching internships with query: {}", query);
    return internshipRepository.findByTitleContainingIgnoreCase(query, pageable);
  }

  public Internship create(Internship internship) {
    logger.info("Creating new internship: {}", internship.getTitle());
    return internshipRepository.save(internship);
  }

  public Internship get(UUID id) {
    logger.debug("Fetching internship with ID: {}", id);
    return internshipRepository.findById(id).orElse(null);
  }

  public Internship update(UUID id, Internship internship) {
    logger.info("Updating internship with ID: {}", id);
    internship.setId(id);
    return internshipRepository.save(internship);
  }

  public void delete(UUID id) {
    logger.info("Deleting internship with ID: {}", id);
    internshipRepository.deleteById(id);
  }

  /**
   * Gets internships for a specific company user.
   *
   * @param companyUserEmail email of the company user
   * @param pageable pagination parameters
   * @return page of internships
   */
  public Page<Internship> getByCompanyEmail(String companyUserEmail, Pageable pageable) {
    logger.debug("Fetching internships for company user: {}", companyUserEmail);
    return internshipRepository.findByCompanyProfile_User_Email(companyUserEmail, pageable);
  }
}
