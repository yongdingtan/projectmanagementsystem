package com.yd.projectmanagementsystem.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yd.projectmanagementsystem.model.Project;
import com.yd.projectmanagementsystem.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User findUserByFullName(String fullName);
	
	User findUserByEmail(String email);
	
	User findUserByPaymentId(String paymentId);
	
    @Query("SELECT t.project FROM Team t JOIN t.members u WHERE u.id = :userId")
    List<Project> findProjectsByUserId(Long userId);
	

}
