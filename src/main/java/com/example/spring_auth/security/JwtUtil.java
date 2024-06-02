package com.example.spring_auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
public class JwtUtil {
    private final Environment env;

    // Secret key to sign the jwt
    private String SECRET_KEY;

    // Expiry time for tokens
    private static final int expireInMs = 300 * 1000;

    @Autowired
    public JwtUtil(Environment env) {
        this.env = env;
        this.SECRET_KEY = env.getProperty("jwtKey", "someDefaultkeys");
    }

    // Generate token
    public String generateToken(String username) {
        System.out.println("Generating token");
        String token = "";
        try {
            token = Jwts.builder()
                    // Add username
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expireInMs))
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                    .compact();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println(token);
        return token;
    }

    // Extract all pieces of info in the jwt payload
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    // Extract set claim from JWT
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    // Extract username from JWT
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Retrieve expiration claim of jwt
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // check if token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate token
    public Boolean validateToken(String token) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername != null && !isTokenExpired(token));
    }
}