package internconnect.au.rw.internconnect.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import org.springframework.stereotype.Service;

import internconnect.au.rw.internconnect.model.Internship;
import internconnect.au.rw.internconnect.repository.InternshipRepository;

@Service
public class InternshipService {

    private final InternshipRepository repo;

    public InternshipService(InternshipRepository repo) {
        this.repo = repo;
    }

    public Page<Internship> search(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return repo.findAll(pageable);
        }
        return repo.findByTitleContainingIgnoreCase(q, pageable);
    }

    public Internship create(Internship i) {
        return repo.save(i);
    }

    public Internship get(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public Internship update(UUID id, Internship i) {
        i.setId(id);
        return repo.save(i);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }

    public Page<Internship> mine(String companyUserEmail, Pageable pageable) {
        return repo.findByCompanyProfile_User_Email(companyUserEmail, pageable);
    }
}
