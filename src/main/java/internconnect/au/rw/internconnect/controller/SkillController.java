package internconnect.au.rw.internconnect.controller;

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

import internconnect.au.rw.internconnect.model.Skill;
import internconnect.au.rw.internconnect.service.SkillService;
import java.util.UUID;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin
public class SkillController {

    private final SkillService service;

    public SkillController(SkillService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Skill> list(Pageable pageable) {
        return service.list(pageable);
    }

    @PostMapping
    public Skill create(@RequestBody Skill s) {
        return service.create(s);
    }

    @GetMapping("/{id}")
    public Skill get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Skill update(@PathVariable UUID id, @RequestBody Skill s) {
        return service.update(id, s);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}


