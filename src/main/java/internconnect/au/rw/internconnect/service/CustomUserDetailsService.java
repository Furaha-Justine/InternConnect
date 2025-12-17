package internconnect.au.rw.internconnect.service;

import java.util.List;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import internconnect.au.rw.internconnect.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var u = userRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var authorities = List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + u.getRole().name()));
        return new User(u.getEmail(), u.getPassword(), authorities);
    }
}


