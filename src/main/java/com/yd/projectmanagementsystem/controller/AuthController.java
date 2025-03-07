package com.yd.projectmanagementsystem.controller;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yd.projectmanagementsystem.config.JwtProvider;
import com.yd.projectmanagementsystem.exception.EmailAlreadyInUseException;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.repository.UserRepository;
import com.yd.projectmanagementsystem.request.LoginRequest;
import com.yd.projectmanagementsystem.response.AuthResponse;
import com.yd.projectmanagementsystem.service.SubscriptionService;
import com.yd.projectmanagementsystem.service.UserDetailsImpl;
import com.yd.projectmanagementsystem.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsImpl userDetails;

    @Autowired
    private SubscriptionService subscriptionService;
    
    @Autowired
    private UserService userService;
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Validated @RequestBody User user, BindingResult bindingResult) throws Exception {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        // Check if the email is already in use
        User isUserExist = userService.findUserByEmail(user.getEmail());
        if (isUserExist != null) {
            throw new EmailAlreadyInUseException("Email already in use by another account");
        }

        // Create a new user
        User createdUser = new User();
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
        createdUser.setEmail(user.getEmail());
        createdUser.setFullName(user.getFullName());

        // Assign a default role (e.g., "ROLE_USER")
        createdUser.setRoles(Set.of("ROLE_USER")); // Use Set.of to create an immutable set

        // Save the user to the database
        User savedUser = userRepository.save(createdUser);
        // Create a subscription for the user (if applicable)
        subscriptionService.createSubscription(savedUser);

        // Prepare the response
        AuthResponse res = new AuthResponse();
        res.setMessage("Signup success. Please log in.");

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Validated @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(username, password);
        String jwt = JwtProvider.generateToken(authentication);

        AuthResponse res = new AuthResponse();
        res.setMessage("Sign in success");
        res.setJwt(jwt);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    private Authentication authenticate(String email, String password) {
        logger.info("Attempting to authenticate user: {}", email);
        UserDetails customUserDetails = userDetails.loadUserByUsername(email);
        if (customUserDetails == null) {
            logger.error("User not found: {}", email);
            throw new BadCredentialsException("Invalid credentials");
        }
        if (!passwordEncoder.matches(password, customUserDetails.getPassword())) {
            logger.error("Invalid password for user: {}", email);
            throw new BadCredentialsException("Invalid password");
        }

        logger.info("User authenticated successfully: {}", email);
        return new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
    }
}