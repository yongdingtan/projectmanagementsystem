package com.yd.projectmanagementsystem.config;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenValidated extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String jwt = request.getHeader(JwtConstant.JWT_HEADER);
		
		
		
		if (jwt != null) {
		    jwt = jwt.substring(7); // Remove "Bearer " prefix if present

		    try {
		        // Create a SecretKey for HMAC signing
		        SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

		        // Parse and validate the JWT token
		        Claims claims = Jwts.parser()
				        	    .verifyWith(key)
				        	    .build()
				        	    .parseSignedClaims(jwt)
				        	    .getPayload();

		        // Retrieve claims
		        String email = claims.get("email", String.class);
		        String authorities = claims.get("authorities", String.class);

		        // Convert authorities to a list of GrantedAuthority
		        List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

		        // Set authentication in the security context
		        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auths);
		        SecurityContextHolder.getContext().setAuthentication(authentication);

		    } catch (ExpiredJwtException e) {
		        throw new BadCredentialsException("Token has expired", e);
		    } catch (JwtException e) {
		        throw new BadCredentialsException("Invalid Token", e);
		    } catch (Exception e) {
		        throw new BadCredentialsException("Token processing error", e);
		    }
		}

		
		filterChain.doFilter(request, response);
		
	}



}
