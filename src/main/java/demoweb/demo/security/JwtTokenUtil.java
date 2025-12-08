package demoweb.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.inactivity.timeout}")
    private Long inactivityTimeout;

    @Value("${jwt.refresh.threshold}")
    private Long refreshThreshold;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(UserDetails userDetails, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("lastActivity", System.currentTimeMillis());
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String refreshToken(String token) {
        Claims claims = extractAllClaims(token);
        claims.put("lastActivity", System.currentTimeMillis());
        return createToken(new HashMap<>(claims), claims.getSubject());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Long extractLastActivity(String token) {
        Object lastActivity = extractAllClaims(token).get("lastActivity");
        if (lastActivity instanceof Integer) {
            return ((Integer) lastActivity).longValue();
        }
        return (Long) lastActivity;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean isInactive(String token) {
        Long lastActivity = extractLastActivity(token);
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastActivity) > inactivityTimeout;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && !isInactive(token));
    }

    public Boolean shouldRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date issuedAt = claims.getIssuedAt();
            long tokenAge = System.currentTimeMillis() - issuedAt.getTime();
            return tokenAge >= refreshThreshold;
        } catch (Exception e) {
            return false;
        }
    }
}