package internconnect.au.rw.internconnect.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import org.springframework.stereotype.Service;

import internconnect.au.rw.internconnect.model.CompanyProfile;
import internconnect.au.rw.internconnect.repository.CompanyProfileRepository;

@Service
public class CompanyProfileService {

    private final CompanyProfileRepository repo;

    public CompanyProfileService(CompanyProfileRepository repo) {
        this.repo = repo;
    }

    public Page<CompanyProfile> list(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public CompanyProfile create(CompanyProfile c) {
        return repo.save(c);
    }

    public CompanyProfile get(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public CompanyProfile update(UUID id, CompanyProfile c) {
        CompanyProfile existing = repo.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setCompanyName(c.getCompanyName());
        existing.setIndustry(c.getIndustry());
        existing.setWebsite(c.getWebsite());
        return repo.save(existing);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}


