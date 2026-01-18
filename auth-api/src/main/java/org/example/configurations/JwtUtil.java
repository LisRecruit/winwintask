package org.example.configurations;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public UUID extractUserId(String token) {
        String id = extractClaim(token, claims -> claims.get("user_id", String.class));
        return UUID.fromString(id);
    }



    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken (UserDetails userDetails, UUID userId) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId.toString());
        return createToken(claims, userDetails.getUsername());
    }
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    Boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            long currentTime = System.currentTimeMillis();
            long expirationTime = expiration.getTime();
            long allowedClockSkewMillis = 1;
            return expirationTime < (currentTime - allowedClockSkewMillis);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return true;
        }
    }


    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .setAllowedClockSkewSeconds(5)
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }
    public Long getUserIdFromToken(String token) {

        final Claims claims = getAllClaimsFromToken(token);
        return claims.get("user_id", Long.class); // Извлекаем userId
    }
}
