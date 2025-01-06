package com.yd.projectmanagementsystem.config;

import java.util.Date;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtProvider {
	
	public static String generateToken(Authentication auth) {
		
        return Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .claim("email", auth.getName())
                .signWith(JwtUtil.getSigningKey())
                .compact();
    }
	
	public static String getEmailFromToken(String jwt) {
		
	    Claims claims = Jwts.parser().verifyWith(JwtUtil.getSigningKey()).build().parseSignedClaims(jwt).getPayload();
	    return String.valueOf(claims.get("email"));
	    
	}

}
