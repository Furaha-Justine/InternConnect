package internconnect.au.rw.internconnect.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.UUID;

import internconnect.au.rw.internconnect.model.Application;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    boolean existsByStudentProfileIdAndInternshipId(UUID studentId, UUID internshipId);

    Page<Application> findByStudentProfileId(UUID studentId, Pageable pageable);

    Page<Application> findByInternshipId(UUID internshipId, Pageable pageable);

    @Query("select a from Application a where a.internship.companyProfile.user.email = :email")
    Page<Application> findByCompanyUserEmail(String email, Pageable pageable);
}
