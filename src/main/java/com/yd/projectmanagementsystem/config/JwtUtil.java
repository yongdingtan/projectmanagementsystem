package com.yd.projectmanagementsystem.config;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;

public class JwtUtil {
    // Static SecretKey, initialized using a secure Base64 key from constants
    private static final SecretKey KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(JwtConstant.SECRET_KEY));

    // Getter for signing key
    public static SecretKey getSigningKey() {
        return KEY;
    }
}
