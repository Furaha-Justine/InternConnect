package internconnect.au.rw.internconnect.controller;

import internconnect.au.rw.internconnect.model.Skill;
import internconnect.au.rw.internconnect.service.SkillService;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for skill management endpoints.
 */
@RestController
@RequestMapping("/api/skills")
@CrossOrigin
public class SkillController {

  private static final Logger logger = LoggerFactory.getLogger(SkillController.class);

  private final SkillService skillService;

  public SkillController(SkillService skillService) {
    this.skillService = skillService;
  }

  @GetMapping
  public Page<Skill> list(Pageable pageable) {
    logger.debug("Listing all skills");
    return skillService.list(pageable);
  }

  @PostMapping
  public Skill create(@RequestBody Skill skill) {
    logger.info("Creating new skill: {}", skill.getName());
    return skillService.create(skill);
  }

  @GetMapping("/{id}")
  public Skill get(@PathVariable UUID id) {
    logger.debug("Fetching skill with ID: {}", id);
    return skillService.get(id);
  }

  @PutMapping("/{id}")
  public Skill update(@PathVariable UUID id, @RequestBody Skill skill) {
    logger.info("Updating skill with ID: {}", id);
    return skillService.update(id, skill);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable UUID id) {
    logger.info("Deleting skill with ID: {}", id);
    skillService.delete(id);
  }
}


