package com.yd.projectmanagementsystem.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.projectmanagementsystem.model.Invitation;
import com.yd.projectmanagementsystem.repository.InvitationRepository;

import jakarta.mail.MessagingException;

@Service
public class InvitationServiceImpl implements InvitationService{

	@Autowired(required=false)
	private InvitationRepository invitationRepository;
	
	@Autowired(required=false)
	private EmailService emailService;
	
	//Production
	private final String url = "https://projectmanagementsystem-frontend.onrender.com";

	//Local
	//private final String url = "http://localhost:4173";
	
	@Override
	public void sendInvitation(String email, Long projectId) throws MessagingException {
		
		String invitationToken = UUID.randomUUID().toString();
		
		Invitation invitation = new Invitation();
		invitation.setEmail(email);
		invitation.setProjectId(projectId);
		invitation.setToken(invitationToken);
		
		invitationRepository.save(invitation);
		
		String invitationLink = url + "/api/project/accept_invitation?token="+invitationToken;
		emailService.sendEmailWithToken(email, invitationLink);
		
	}

	@Override
	public Invitation acceptInvitation(String token, Long userId) throws Exception {
		
		Invitation invitation = invitationRepository.findByToken(token);
		if(invitation == null) {
			throw new Exception("Invalid invitation token");
		}
		
		return invitation;
	}

	@Override
	public String getTokenByUserEmail(String userEmail) {
		
		return invitationRepository.findByEmail(userEmail).getToken();
	}

	@Override
	public void deleteToken(String token) {
		
		invitationRepository.delete(invitationRepository.findByToken(token));
		
	}

}
