package internconnect.au.rw.internconnect.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;
import org.springframework.stereotype.Service;

import internconnect.au.rw.internconnect.model.Skill;
import internconnect.au.rw.internconnect.repository.SkillRepository;

@Service
public class SkillService {

    private final SkillRepository repo;

    public SkillService(SkillRepository repo) {
        this.repo = repo;
    }

    public Page<Skill> list(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Skill create(Skill s) {
        return repo.save(s);
    }

    public Skill get(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public Skill update(UUID id, Skill s) {
        s.setId(id);
        return repo.save(s);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}


