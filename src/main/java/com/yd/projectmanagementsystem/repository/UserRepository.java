package com.yd.projectmanagementsystem.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.yd.projectmanagementsystem.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByEmail(String email);
	
	User findUserByPaymentId(String paymentId);
	

}
