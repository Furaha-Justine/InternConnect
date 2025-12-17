package internconnect.au.rw.internconnect.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import internconnect.au.rw.internconnect.model.Role;
import internconnect.au.rw.internconnect.model.User;
import internconnect.au.rw.internconnect.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepo,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public User register(String firstName, String lastName, String email, String rawPassword, Role role) {
        User u = new User();
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setEmail(email);
        u.setRole(role);
        u.setPassword(passwordEncoder.encode(rawPassword));
        return userRepo.save(u);
    }

    public String login(String email, String rawPassword) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, rawPassword));
        User u = userRepo.findByEmail(email).orElseThrow();
        return jwtService.generateToken(u.getId(), u.getEmail(), u.getRole().name());
    }
}


