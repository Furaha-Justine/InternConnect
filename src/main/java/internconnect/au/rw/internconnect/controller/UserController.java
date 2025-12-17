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
import java.util.UUID;

import internconnect.au.rw.internconnect.model.Role;
import internconnect.au.rw.internconnect.model.User;
import internconnect.au.rw.internconnect.service.UserService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<User> list(Pageable pageable) {
        return service.listAll(pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public User create(@RequestBody User u) {
        return service.create(u);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable UUID id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User me = service.findByEmail(email);
        if (me == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
       
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
            .getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !id.equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        return service.get(id);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable UUID id, @RequestBody User u) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User me = service.findByEmail(email);
        if (me == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
      
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
            .getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !id.equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
       
        if (!isAdmin && u.getRole() != null && !u.getRole().equals(me.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot change role");
        }
        return service.update(id, u);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @GetMapping("/by-province-name")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<User> byProvinceName(@RequestParam String name, Pageable pageable) {
        return service.byProvinceName(name, pageable);
    }

    @GetMapping("/by-province-code")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<User> byProvinceCode(@RequestParam String code, Pageable pageable) {
        return service.byProvinceCode(code, pageable);
    }

    @GetMapping("/by-role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<User> byRole(@RequestParam Role role, Pageable pageable) {
        return service.byRole(role, pageable);
    }

    @GetMapping("/{id}/location")
    public Map<String, Object> userLocation(@PathVariable UUID id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User me = service.findByEmail(email);
        if (me == null || !id.equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        return service.loadLocationFor(me);
    }
}
