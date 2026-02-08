package com.paypal.user_service.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Component
public class JWTUtil {

    private static final String SECRET = "secret123secret123secret123secret123secret123secret123";

    // Used to covert our key to a proper signed key
    private Key getSigninKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // Function to fetch email id from the token
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .getSubject();
    }

    // To check if the token is valid or not
    public boolean validateToken(String token, String userName) {
        try {
            extractEmail(token); // If parsing succeeds, token is valid
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .getSubject();
    }

    public  String generateToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+86400000))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractRole(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .get("role");
    }
}
