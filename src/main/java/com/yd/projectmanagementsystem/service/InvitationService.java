package com.yd.projectmanagementsystem.service;

import com.yd.projectmanagementsystem.model.Invitation;

import jakarta.mail.MessagingException;

public interface InvitationService {
	
	public void sendInvitation(String email, Long projectId) throws MessagingException;
	
	public Invitation acceptInvitation(String token, Long userId) throws Exception;
	
	public String getTokenByUserEmail(String userEmail);
	
	void deleteToken(String token);
	
}
