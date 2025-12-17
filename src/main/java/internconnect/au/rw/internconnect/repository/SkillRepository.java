package internconnect.au.rw.internconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import internconnect.au.rw.internconnect.model.Skill;

public interface SkillRepository extends JpaRepository<Skill, UUID> {
}


