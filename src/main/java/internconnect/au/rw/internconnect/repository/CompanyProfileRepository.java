package internconnect.au.rw.internconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import internconnect.au.rw.internconnect.model.CompanyProfile;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, UUID> {
}
