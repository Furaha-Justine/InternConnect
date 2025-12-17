package internconnect.au.rw.internconnect.service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${app.jwt.secret:changeme-secret-key-please}") String secret) {
        byte[] bytes = secret.getBytes();
        if (bytes.length < 32) {
            this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            this.secretKey = Keys.hmacShaKeyFor(bytes);
        }
    }

    public String generateToken(java.util.UUID userId, String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(email)
            .addClaims(Map.of("uid", userId, "role", role))
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(60 * 60 * 8)))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public io.jsonwebtoken.Claims parse(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}


