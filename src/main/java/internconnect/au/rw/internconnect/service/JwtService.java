package internconnect.au.rw.internconnect.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for JWT token generation and parsing.
 */
@Service
public class JwtService {

  private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
  private static final int MIN_SECRET_KEY_LENGTH = 32;
  private static final int TOKEN_EXPIRATION_HOURS = 8;
  private static final int SECONDS_PER_HOUR = 3600;
  private static final String USER_ID_CLAIM = "uid";
  private static final String ROLE_CLAIM = "role";

  private final SecretKey secretKey;

  public JwtService(@Value("${app.jwt.secret:changeme-secret-key-please}") String secret) {
    byte[] secretBytes = secret.getBytes();
    if (secretBytes.length < MIN_SECRET_KEY_LENGTH) {
      logger.warn("Secret key is too short, generating a new one");
      this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    } else {
      this.secretKey = Keys.hmacShaKeyFor(secretBytes);
    }
  }

  /**
   * Generates a JWT token for a user.
   *
   * @param userId user's unique identifier
   * @param email user's email address
   * @param role user's role
   * @return JWT token string
   */
  public String generateToken(UUID userId, String email, String role) {
    logger.debug("Generating JWT token for user: {}", email);
    Instant now = Instant.now();
    String token = Jwts.builder().setSubject(email)
        .addClaims(Map.of(USER_ID_CLAIM, userId, ROLE_CLAIM, role))
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plusSeconds(TOKEN_EXPIRATION_HOURS * SECONDS_PER_HOUR)))
        .signWith(secretKey, SignatureAlgorithm.HS256).compact();
    logger.debug("JWT token generated successfully for user: {}", email);
    return token;
  }

  /**
   * Parses and validates a JWT token.
   *
   * @param token JWT token string
   * @return claims from the token
   * @throws io.jsonwebtoken.JwtException if token is invalid
   */
  public Claims parse(String token) {
    logger.debug("Parsing JWT token");
    return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
  }
}


