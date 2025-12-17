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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import internconnect.au.rw.internconnect.model.Application;
import internconnect.au.rw.internconnect.service.ApplicationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;
import internconnect.au.rw.internconnect.service.StudentProfileService;
import internconnect.au.rw.internconnect.service.InternshipService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin
public class ApplicationController {

    private final ApplicationService service;
    private final StudentProfileService studentProfileService;
    private final InternshipService internshipService;

    public ApplicationController(ApplicationService service, StudentProfileService studentProfileService, InternshipService internshipService) {
        this.service = service;
        this.studentProfileService = studentProfileService;
        this.internshipService = internshipService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public Application apply(@RequestBody Application a) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var myProfile = studentProfileService.findByUserEmail(email);
        if (myProfile == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Create your student profile first");
        }

        if (a.getStudentProfile() != null && a.getStudentProfile().getId() != null
                && !myProfile.getId().equals(a.getStudentProfile().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only apply with your own profile");
        }
        if (a.getInternship() == null || a.getInternship().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Internship id is required");
        }
        var internshipId = a.getInternship().getId();
        if (service.alreadyApplied(myProfile.getId(), internshipId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already applied to this internship");
        }
        a.setStudentProfile(myProfile);
        a.setAppliedAt(java.time.Instant.now());
        return service.apply(a);
    }

    @GetMapping
    public Page<Application> list(Pageable pageable) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            boolean isStudent = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
            boolean isCompany = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_COMPANY"));
            if (isStudent) {
                var profile = studentProfileService.findByUserEmail(email);
                if (profile != null) {
                    return service.forStudent(profile.getId(), pageable);
                }
                return Page.empty(pageable);
            }
            if (isCompany) {
                return service.forCompanyEmail(email, pageable);
            }
        }
        return service.list(pageable);
    }

    @GetMapping("/student/{studentId}")
    public Page<Application> byStudent(@PathVariable UUID studentId, Pageable pageable) {
        return service.forStudent(studentId, pageable);
    }

    @GetMapping("/internship/{internshipId}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public Page<Application> byInternship(@PathVariable UUID internshipId, Pageable pageable) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        var internship = internshipService.get(internshipId);
        if (internship == null || internship.getCompanyProfile() == null ||
            internship.getCompanyProfile().getUser() == null ||
            !email.equals(internship.getCompanyProfile().getUser().getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        return service.forInternship(internshipId, pageable);
    }

    @GetMapping("/{id}")
    public Application get(@PathVariable UUID id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public Application update(@PathVariable UUID id, @RequestBody Application a) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        var existing = service.get(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found");
        }
        var internship = existing.getInternship();
        if (internship == null || internship.getCompanyProfile() == null ||
            internship.getCompanyProfile().getUser() == null ||
            !email.equals(internship.getCompanyProfile().getUser().getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        existing.setStatus(a.getStatus());
        return service.update(id, existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public Page<Application> myApplications(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var profile = studentProfileService.findByUserEmail(email);
        if (profile == null) {
            return Page.empty(pageable);
        }
        return service.forStudent(profile.getId(), pageable);
    }

    @GetMapping("/company")
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    public Page<Application> applicationsForMyCompany(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return service.forCompanyEmail(email, pageable);
    }
}
