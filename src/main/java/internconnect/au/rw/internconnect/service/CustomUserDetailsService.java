package internconnect.au.rw.internconnect.service;

import internconnect.au.rw.internconnect.model.User;
import internconnect.au.rw.internconnect.repository.UserRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom user details service for Spring Security authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
  private static final String ROLE_PREFIX = "ROLE_";
  private static final String USER_NOT_FOUND_MESSAGE = "User not found";

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    logger.debug("Loading user details for: {}", username);
    User user = userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));
    SimpleGrantedAuthority authority =
        new SimpleGrantedAuthority(ROLE_PREFIX + user.getRole().name());
    List<SimpleGrantedAuthority> authorities = List.of(authority);
    logger.debug("User details loaded successfully for: {}", username);
    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .authorities(authorities)
        .build();
  }
}


