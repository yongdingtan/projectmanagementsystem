package com.yd.projectmanagementsystem.service;

import com.yd.projectmanagementsystem.model.User;

public interface UserService {
	
	User findUserProfileByJwt(String jwt) throws Exception;
	
	User findUserByEmail(String emailId) throws Exception;
	
	User findUserById(Long userId) throws Exception;
	
	User updateUserProjectSize(User user, int number);
	
}
