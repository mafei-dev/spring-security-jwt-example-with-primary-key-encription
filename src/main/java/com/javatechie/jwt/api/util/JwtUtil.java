package com.javatechie.jwt.api.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("${auth.jwt.secret}")
    private String secret;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date getIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }


    public String extractUID(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("uid-token").toString();
    }

    public String extractUserUID(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("userUID").toString();
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(String username, String uidToken, String userUID, Date IssuedAt) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid-token", uidToken);
        claims.put("userUID", userUID);
        return createToken(claims, username, IssuedAt);
    }

    private String createToken(Map<String, Object> claims, String subject, Date IssuedAt) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(IssuedAt)
                .setExpiration(new Date(IssuedAt.getTime() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

