package com.yd.projectmanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yd.projectmanagementsystem.config.JwtProvider;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.repository.UserRepository;
import com.yd.projectmanagementsystem.request.LoginRequest;
import com.yd.projectmanagementsystem.response.AuthResponse;
import com.yd.projectmanagementsystem.service.SubscriptionService;
import com.yd.projectmanagementsystem.service.UserDetailsImpl;

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
	
	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
		User isUserExist = userRepository.findByEmail(user.getEmail());
		
		if(isUserExist!=null) {
			throw new Exception("Email already in use by another account");
		}
		
		User createdUser = new User();
		createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
		createdUser.setEmail(user.getEmail());
		createdUser.setFullName(user.getFullName());
		
		User savedUser = userRepository.save(createdUser);
		subscriptionService.createSubscription(savedUser);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = JwtProvider.generateToken(authentication);
		
		AuthResponse res = new AuthResponse();
		res.setMessage("Signup success");
		res.setJwt(jwt);
		
		return new ResponseEntity<>(res, HttpStatus.CREATED);
	}

	
	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) {
		
		String username = loginRequest.getEmail();
		String password = loginRequest.getPassword();
		
		Authentication authentication = authenticate(username, password);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = JwtProvider.generateToken(authentication);
		
		AuthResponse res = new AuthResponse();
		res.setMessage("Sign in success");
		res.setJwt(jwt);
		
		return new ResponseEntity<>(res, HttpStatus.CREATED);
		
		
	}


	private Authentication authenticate(String username, String password) {
		
		UserDetails customUserDetails = userDetails.loadUserByUsername(username);
		if(customUserDetails == null) {
			throw new BadCredentialsException("Invalid credentials");
		}
		if(!passwordEncoder.matches(password, customUserDetails.getPassword())) {
			throw new BadCredentialsException("Invalid password");
		}
		
		return new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
	}

}
