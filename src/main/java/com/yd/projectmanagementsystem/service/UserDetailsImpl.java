package com.yd.projectmanagementsystem.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.repository.UserRepository;

@Service
public class UserDetailsImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) {
        
    	User user;
		try {
			user = userService.findUserByEmail(email);
	        if (user == null) {
	            throw new UsernameNotFoundException("User not found with email: " + email);
	        }

	        // Map roles to GrantedAuthority
	        List<GrantedAuthority> authorities = new ArrayList<>();
	        Set<String> roles = user.getRoles();
	        if (roles != null) {
	            for (String role : roles) {
	                authorities.add(new SimpleGrantedAuthority(role));
	            }
	        }

	        return new org.springframework.security.core.userdetails.User(
	                user.getEmail(),
	                user.getPassword(),
	                authorities
	        );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

    }

}

