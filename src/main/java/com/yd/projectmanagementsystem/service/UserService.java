package com.yd.projectmanagementsystem.service;

import java.util.List;

import com.yd.projectmanagementsystem.model.Project;
import com.yd.projectmanagementsystem.model.User;

public interface UserService {
	
	User findUserProfileByJwt(String jwt) throws Exception;
	
	User findUserByFullName(String fullName) throws Exception;
	
	User findUserByEmail(String email) throws Exception;
	
	User findUserById(Long userId) throws Exception;
	
	User findUserByPaymentId(String paymentId) throws Exception;
	
	User updateUserProjectSize(User user, int number);
	
	User saveUser(User user);
	
	List<Project> getProjectsForUser(Long userId);
	
	void updateUserPaymentId(User user, String paymentId) throws Exception;
	
}
