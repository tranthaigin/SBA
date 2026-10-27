package edu.slot25.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final javax.crypto.SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms}") long expirationMs
    ) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        if (expirationMs <= 0) {
            throw new IllegalArgumentException("security.jwt.expiration-ms must be positive");
        }
        this.expirationMs = expirationMs;
    }

    public String generateToken(UserDetails user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(JwtService::roleName)
                        .sorted()
                        .toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(signingKey)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isValid(String token, UserDetails user) {
        Claims claims = parseClaims(token);
        Object roleClaim = claims.get("roles");
        if (!(roleClaim instanceof List<?> roles)) {
            return false;
        }

        List<String> currentRoles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(JwtService::roleName)
                .sorted()
                .toList();
        Date expiration = claims.getExpiration();
        return user.getUsername().equals(claims.getSubject())
                && expiration != null
                && expiration.after(new Date())
                && roles.equals(currentRoles);
    }

    private static String roleName(String authority) {
        if (!authority.startsWith("ROLE_") || authority.length() == 5) {
            throw new IllegalArgumentException("JWT roles must use the ROLE_ authority prefix");
        }
        return authority.substring(5);
    }
}
