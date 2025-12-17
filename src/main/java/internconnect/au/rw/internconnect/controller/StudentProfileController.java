package internconnect.au.rw.internconnect.controller;

import java.util.List;
import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import internconnect.au.rw.internconnect.model.StudentProfile;
import internconnect.au.rw.internconnect.service.StudentProfileService;
import internconnect.au.rw.internconnect.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/student-profiles")
@CrossOrigin
public class StudentProfileController {

    private final StudentProfileService service;
    private final UserService userService;

    public StudentProfileController(StudentProfileService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping
    public Page<StudentProfile> list(@RequestParam(required = false) String q, Pageable pageable) {
        return service.list(q, pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public StudentProfile create(@RequestBody StudentProfile s) {
      
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var currentUser = userService.findByEmail(email);
        s.setUser(currentUser);
        return service.create(s);
    }

    @GetMapping("/{id}")
    public StudentProfile get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public StudentProfile update(@PathVariable UUID id, @RequestBody StudentProfile s) {
        return service.update(id, s);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PostMapping("/{id}/skills")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public StudentProfile addSkills(@PathVariable UUID id, @RequestBody List<UUID> skillIds) {
        return service.addSkills(id, skillIds);
    }
}


