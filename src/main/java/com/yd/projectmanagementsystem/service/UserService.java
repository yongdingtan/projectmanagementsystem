package com.yd.projectmanagementsystem.service;

import com.yd.projectmanagementsystem.model.User;

public interface UserService {
	
	User findUserProfileByJwt(String jwt) throws Exception;
	
	User findUserByFullName(String fullName) throws Exception;
	
	User findUserByEmail(String email) throws Exception;
	
	User findUserById(Long userId) throws Exception;
	
	User findUserByPaymentId(String paymentId) throws Exception;
	
	User updateUserProjectSize(User user, int number);
	
	void updateUserPaymentId(User user, String paymentId) throws Exception;
	
}
