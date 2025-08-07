package com.bankingsystem.user.config;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtService {

    private static final String SECRET_KEY = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b";

    private static final Long EXPIRATION_TIME = 86400000L;

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiration = now.plus(EXPIRATION_TIME, ChronoUnit.MILLIS);

        String authorities = authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("roles", authorities)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(getKey())
                .compact();
    }

    
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }


    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("JWT expired: " + e.getMessage());
        } catch (io.jsonwebtoken.security.SecurityException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            System.out.println("Malformed JWT: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        }
        return false;
    }


    private Claims parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date expiration = claims.getExpiration();
        if (expiration.before(new Date())) {
            throw new io.jsonwebtoken.ExpiredJwtException(null, claims, "Token expired");
        }

        return claims;
    }


    public long getExpirationMs() {
        return EXPIRATION_TIME;
    }
}
