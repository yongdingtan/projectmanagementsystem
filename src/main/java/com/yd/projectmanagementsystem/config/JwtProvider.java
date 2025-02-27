package com.yd.projectmanagementsystem.config;

import java.util.Date;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtProvider {

	public static String generateToken(Authentication auth) {

		return Jwts.builder().issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 86400000)) // 24
																												// hours
				.claim("email", auth.getName()).signWith(JwtUtil.getSigningKey()).compact();
	}

	public static String getEmailFromToken(String jwt) {

		jwt = jwt.substring(7);
		Claims claims = Jwts.parser().verifyWith(JwtUtil.getSigningKey()).build().parseSignedClaims(jwt).getPayload();
		return claims.get("email", String.class);

	}

	public static boolean validateToken(String token) {
		try {
			// Parse the token and check its signature and claims
			Claims claims = Jwts.parser().verifyWith(JwtUtil.getSigningKey()).build().parseSignedClaims(token)
					.getPayload();

			// Check if the token is expired
			Date expirationDate = claims.getExpiration();
			return !expirationDate.before(new Date()); // Token is valid if not expired
		} catch (Exception e) {
			// Token is invalid (e.g., malformed, expired, or signature is incorrect)
			return false;
		}
	}

}
