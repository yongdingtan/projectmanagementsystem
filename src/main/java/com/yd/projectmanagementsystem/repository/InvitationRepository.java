package com.yd.projectmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yd.projectmanagementsystem.model.Invitation;

public interface InvitationRepository extends JpaRepository<Invitation, Long>{
	
	Invitation findByToken(String token);
	
	Invitation findByEmail(String userEmail);
}
