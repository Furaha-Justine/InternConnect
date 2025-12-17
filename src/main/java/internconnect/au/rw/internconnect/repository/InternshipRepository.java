package internconnect.au.rw.internconnect.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import internconnect.au.rw.internconnect.model.Internship;

public interface InternshipRepository extends JpaRepository<Internship, UUID> {

    Page<Internship> findByTitleContainingIgnoreCase(String q, Pageable pageable);

    Page<Internship> findByCompanyProfileId(UUID companyId, Pageable pageable);

    Page<Internship> findByCompanyProfile_User_Email(String email, Pageable pageable);
}
