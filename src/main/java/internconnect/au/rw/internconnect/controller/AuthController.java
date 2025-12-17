package internconnect.au.rw.internconnect.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import internconnect.au.rw.internconnect.model.Role;
import internconnect.au.rw.internconnect.model.User;
import internconnect.au.rw.internconnect.repository.UserRepository;
import internconnect.au.rw.internconnect.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.AuthenticationException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    public static record RegisterRequest(String firstName, String lastName, String email, String password, Role role) {}
    public static record LoginRequest(String email, String password) {}
    public static record TokenResponse(String token) {}

    private final AuthService authService;
    private final UserRepository userRepo;

    public AuthController(AuthService authService, UserRepository userRepo) {
        this.authService = authService;
        this.userRepo = userRepo;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest req) {
        if (userRepo.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        if (req.role() == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role cannot be registered. Please contact administrator.");
        }
        return authService.register(req.firstName(), req.lastName(), req.email(), req.password(), req.role());
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest req) {
        try {
            String token = authService.login(req.email(), req.password());
            return new TokenResponse(token);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }
}


