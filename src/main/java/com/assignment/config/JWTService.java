package com.assignment.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService  {



    private static String secretKey = "";

    public JWTService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        String role1="ROLE_"+role;
        claims.put("role", role1);  // Add the role to the claims map

        return Jwts.builder()
                .setClaims(claims)  // Directly set the claims map
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 10 * 1000 ))  // Expiry time in 10 min
                .signWith(getKey())  // Sign with the correct key
                .compact();
    }

    public SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);  // Decode the key
        return Keys.hmacShaKeyFor(keyBytes);  // Generate a SecretKey for HMAC-SHA256
    }

    public String extractUserName(String token) {
        // Extract the username from the JWT token
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        // Extract the role from the JWT token
        return extractClaim(token, claims -> claims.get("role", String.class));  // Use "role" key to get the value
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()  // Use parserBuilder() to build the parser
                .setSigningKey(getKey())  // Set the signing key for validation
                .build()
                .parseClaimsJws(token)  // Parse the JWT token (verify signature and extract claims)
                .getBody();  // Get the body (claims) from the JWT
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }









}
