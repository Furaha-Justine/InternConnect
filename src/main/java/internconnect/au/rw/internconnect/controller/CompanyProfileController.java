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

import internconnect.au.rw.internconnect.model.CompanyProfile;
import internconnect.au.rw.internconnect.service.CompanyProfileService;
import org.springframework.security.access.prepost.PreAuthorize;
import internconnect.au.rw.internconnect.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import internconnect.au.rw.internconnect.model.User;
import java.util.UUID;

@RestController
@RequestMapping("/api/company-profiles")
@CrossOrigin
public class CompanyProfileController {

    private final CompanyProfileService service;
    private final UserService userService;

    public CompanyProfileController(CompanyProfileService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @GetMapping
    public Page<CompanyProfile> list(Pageable pageable) {
        return service.list(pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public CompanyProfile create(@RequestBody CompanyProfile c) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findByEmail(email);
        c.setUser(currentUser);
        return service.create(c);
    }

    @GetMapping("/{id}")
    public CompanyProfile get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public CompanyProfile update(@PathVariable UUID id, @RequestBody CompanyProfile c) {
        return service.update(id, c);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}


