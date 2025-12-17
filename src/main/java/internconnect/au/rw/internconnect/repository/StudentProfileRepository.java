package internconnect.au.rw.internconnect.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

import internconnect.au.rw.internconnect.model.StudentProfile;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, UUID> {

    Page<StudentProfile> findByUniversityContainingIgnoreCase(String q, Pageable pageable);

    Optional<StudentProfile> findByUser_Email(String email);
}
