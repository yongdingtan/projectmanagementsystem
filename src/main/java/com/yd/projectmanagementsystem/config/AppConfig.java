package com.yd.projectmanagementsystem.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class AppConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
            .csrf(csrf -> csrf.disable()) // Disable CSRF
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/payment/upgrade_plan/success").permitAll() // Allow unauthenticated access to this endpoint
                .requestMatchers("/auth/signin").permitAll() // Allow unauthenticated access to /auth/signin
                .requestMatchers("/auth/signup").permitAll() // Allow unauthenticated access to /auth/signup
                .requestMatchers("/api/public/**").permitAll() // Allow unauthenticated access to public APIs
                .requestMatchers("/api/**").authenticated() // Require authentication for all other /api/** endpoints
                .anyRequest().authenticated() // Require authentication for all other requests
            )
            .addFilterBefore(new JwtTokenValidated(), BasicAuthenticationFilter.class); // Add JWT filter

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow specific origins (replace with your frontend URL)
        configuration.setAllowedOrigins(Arrays.asList(
        		"https://projectmanagementsystem-frontend.onrender.com",
                "http://localhost:3000", // React (default port)
                "http://localhost:5173", // Vite (default port)
                "http://localhost:4200",  // Angular (default port)
                "http://localhost:4173",
                "http://localhost:8080"
        		));

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for password encoding
    }
}