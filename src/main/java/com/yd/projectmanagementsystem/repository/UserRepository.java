package com.yd.projectmanagementsystem.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.yd.projectmanagementsystem.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User findUserByFullName(String fullName);
	
	User findUserByEmail(String email);
	
	User findUserByPaymentId(String paymentId);
	

}
