package internconnect.au.rw.internconnect.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import org.springframework.stereotype.Service;

import internconnect.au.rw.internconnect.model.Application;
import internconnect.au.rw.internconnect.repository.ApplicationRepository;

@Service
public class ApplicationService {

    private final ApplicationRepository repo;

    public ApplicationService(ApplicationRepository repo) {
        this.repo = repo;
    }

    public Application apply(Application app) {
        return repo.save(app);
    }

    public Page<Application> forStudent(UUID studentId, Pageable pageable) {
        return repo.findByStudentProfileId(studentId, pageable);
    }

    public Page<Application> forInternship(UUID internshipId, Pageable pageable) {
        return repo.findByInternshipId(internshipId, pageable);
    }

    public Application get(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public Page<Application> list(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Application update(UUID id, Application a) {
        a.setId(id);
        return repo.save(a);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }

    public Page<Application> forCompanyEmail(String email, Pageable pageable) {
        return repo.findByCompanyUserEmail(email, pageable);
    }

    public boolean alreadyApplied(UUID studentProfileId, UUID internshipId) {
        return repo.existsByStudentProfileIdAndInternshipId(studentProfileId, internshipId);
    }
}
