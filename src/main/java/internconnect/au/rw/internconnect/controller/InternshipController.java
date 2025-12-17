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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import internconnect.au.rw.internconnect.model.Internship;
import internconnect.au.rw.internconnect.service.InternshipService;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/internships")
@CrossOrigin
public class InternshipController {

    private final InternshipService service;

    public InternshipController(InternshipService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Internship> list(@RequestParam(required = false) String q, Pageable pageable) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            boolean isCompany = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COMPANY"));
            if (isCompany) {
                String email = auth.getName();
                return service.mine(email, pageable);
            }
        }
        return service.search(q, pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public Internship create(@RequestBody Internship i) {
        return service.create(i);
    }

    @GetMapping("/{id}")
    public Internship get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public Internship update(@PathVariable UUID id, @RequestBody Internship i) {
        return service.update(id, i);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public Page<Internship> myInternships(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return service.mine(email, pageable);
    }
}
