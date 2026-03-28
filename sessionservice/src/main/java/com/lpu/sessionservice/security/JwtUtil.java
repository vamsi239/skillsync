package com.lpu.sessionservice.security;

import java.security.Key;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final String SECRET = "secret123secret123secret123secret123";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public Claims validate(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }

    public String getRole(String token) {
        return (String) validate(token).get("role");
    }
}
