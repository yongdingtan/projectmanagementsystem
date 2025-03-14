package com.yd.projectmanagementsystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.projectmanagementsystem.config.JwtProvider;
import com.yd.projectmanagementsystem.model.Project;
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
	public User findUserByFullName(String fullName) throws Exception {
		User user = userRepository.findUserByFullName(fullName);
		return user;
	}

	@Override
	public User findUserByEmail(String email) throws Exception {
		User user = userRepository.findUserByEmail(email);
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

	@Override
	public User findUserByPaymentId(String paymentId) throws Exception {
        // Implement logic to find the user by paymentId
        return userRepository.findUserByPaymentId(paymentId);
    }
	
	@Override
    public void updateUserPaymentId(User user, String paymentId) {
        user.setPaymentId(paymentId); // Update the paymentId
        userRepository.save(user); // Save the updated user
    }

	@Override
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public List<Project> getProjectsForUser(Long userId) {
		return userRepository.findProjectsByUserId(userId);
	}

}