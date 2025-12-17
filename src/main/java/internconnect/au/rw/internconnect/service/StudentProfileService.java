package internconnect.au.rw.internconnect.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import internconnect.au.rw.internconnect.model.Skill;
import internconnect.au.rw.internconnect.model.StudentProfile;
import internconnect.au.rw.internconnect.repository.SkillRepository;
import internconnect.au.rw.internconnect.repository.StudentProfileRepository;

@Service
public class StudentProfileService {

    private final StudentProfileRepository repo;
    private final SkillRepository skillRepo;

    public StudentProfileService(StudentProfileRepository repo, SkillRepository skillRepo) {
        this.repo = repo;
        this.skillRepo = skillRepo;
    }

    public Page<StudentProfile> list(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return repo.findAll(pageable);
        }
        return repo.findByUniversityContainingIgnoreCase(q, pageable);
    }

    public StudentProfile findByUserEmail(String email) {
        return repo.findByUser_Email(email).orElse(null);
    }

    public StudentProfile create(StudentProfile s) {
        return repo.save(s);
    }

    public StudentProfile get(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public StudentProfile update(UUID id, StudentProfile s) {
        StudentProfile existing = repo.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setUniversity(s.getUniversity());
        existing.setMajor(s.getMajor());
        existing.setGraduationYear(s.getGraduationYear());
        existing.setCvUrl(s.getCvUrl());
        if (s.getSkills() != null) {
            existing.setSkills(s.getSkills());
        }
      
        return repo.save(existing);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }

    public StudentProfile addSkills(UUID studentProfileId, List<UUID> skillIds) {
        StudentProfile profile = repo.findById(studentProfileId).orElse(null);
        if (profile == null) {
            return null;
        }
        List<Skill> skills = skillRepo.findAllById(skillIds);
        Set<Skill> current = profile.getSkills();
        if (current == null) {
            current = new HashSet<>();
        }
        current.addAll(skills);
        profile.setSkills(current);
        return repo.save(profile);
    }
}


