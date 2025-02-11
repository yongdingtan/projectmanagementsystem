package com.yd.projectmanagementsystem.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.projectmanagementsystem.config.JwtProvider;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public User findUserProfileByJwt(String jwt) throws Exception {
		String email = JwtProvider.getEmailFromToken(jwt);
		
		return findUserByEmail(email);
	}

	@Override
	public User findUserByEmail(String emailId) throws Exception {
		User user = userRepository.findByEmail(emailId);
		if(user == null) {
			throw new Exception("User not found");
		}
		return user;
	}

	@Override
	public User findUserById(Long userId) throws Exception {
		Optional<User> optionalUser = userRepository.findById(userId);
		if(optionalUser.isEmpty()) {
			throw new Exception("User not found");
		}
		return optionalUser.get();
	}

	@Override
	public User updateUserProjectSize(User user, int number) {
		return userRepository.save(user);
	}
	
	

}