package internconnect.au.rw.internconnect.security;

import internconnect.au.rw.internconnect.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT authentication filter that processes JWT tokens from Authorization header.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private static final int BEARER_PREFIX_LENGTH = 7;

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
    if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
      String token = authorizationHeader.substring(BEARER_PREFIX_LENGTH);
      try {
        var claims = jwtService.parse(token);
        String email = claims.getSubject();
        if (email != null
            && SecurityContextHolder.getContext().getAuthentication() == null) {
          UserDetails userDetails = userDetailsService.loadUserByUsername(email);
          UsernamePasswordAuthenticationToken authenticationToken =
              new UsernamePasswordAuthenticationToken(userDetails, null,
                  userDetails.getAuthorities());
          authenticationToken.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          logger.debug("Successfully authenticated user: {}", email);
        }
      } catch (Exception exception) {
        logger.warn("Failed to parse JWT token: {}", exception.getMessage());
      }
    }
    filterChain.doFilter(request, response);
  }
}


