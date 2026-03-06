package com.author.book_finder.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    private SecretKey getSigningKey() {
        // ensure correct algorithm key length (HS256/HS512 etc)
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateJwtToken(org.springframework.security.core.Authentication authentication) {
        String username = authentication.getName();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())   // non‑deprecated signing
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        Claims claims = Jwts.parser()                     // returns JwtParserBuilder
                .verifyWith(getSigningKey())              // set verification key
                .build()                                  // build parser
                .parseSignedClaims(token)                 // parse signed JWT
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            // same parse call for validation
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // invalid signature, malformed, expired, etc.
        }
        return false;
    }
}
