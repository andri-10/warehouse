package com.example.warehouse.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
public class JwtDebugger {

    @Value("${jwt.secret}")
    private String secret;

    private Key signingKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes();
        signingKey = Keys.hmacShaKeyFor(keyBytes);
        System.out.println("JwtDebugger initialized with secret length: " + secret.length());
    }

    public void printToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            System.out.println("=== JWT TOKEN DEBUG ===");
            System.out.println("Subject: " + claims.getSubject());
            System.out.println("Roles: " + claims.get("roles"));
            System.out.println("Issued At: " + claims.getIssuedAt());
            System.out.println("Expiration: " + claims.getExpiration());
            System.out.println("Current time: " + new Date());
            System.out.println("Is expired: " + claims.getExpiration().before(new Date()));
            System.out.println("All claims: " + claims);
            System.out.println("=====================");
        } catch (Exception e) {
            System.out.println("Error parsing token: " + e.getMessage());
            e.printStackTrace();
        }
    }
}