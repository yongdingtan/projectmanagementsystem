package com.yd.projectmanagementsystem.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yd.projectmanagementsystem.model.Chat;
import com.yd.projectmanagementsystem.model.Invitation;
import com.yd.projectmanagementsystem.model.Project;
import com.yd.projectmanagementsystem.model.Team;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.repository.TeamRepository;
import com.yd.projectmanagementsystem.request.InviteRequest;
import com.yd.projectmanagementsystem.response.MessageResponse;
import com.yd.projectmanagementsystem.service.InvitationService;
import com.yd.projectmanagementsystem.service.ProjectService;
import com.yd.projectmanagementsystem.service.UserService;

import io.jsonwebtoken.JwtException;

@RestController
@RequestMapping("/api/project")
public class ProjectController {
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private InvitationService invitationService;
	
	@GetMapping
	public ResponseEntity<List<Project>> getProjects(
			@RequestParam(required = false)String category,
			@RequestParam(required = false) String tag,
			@RequestHeader("Authorization") String jwt
		) throws Exception {
		
	    if (jwt == null || jwt.isEmpty()) {
	        throw new JwtException("JWT token is missing");
	    }
		User user = userService.findUserProfileByJwt(jwt);
		List<Project> projects = projectService.getProjectByTeam(user, category, tag);
		
		return new ResponseEntity<>(projects, HttpStatus.OK);
	}
	
	@GetMapping("/{projectId}")
	public ResponseEntity<Project> getProjectById(
			@PathVariable Long projectId
		) throws Exception {
		
	    if (projectId == null || projectId <= 0) {
	        throw new IllegalArgumentException("Invalid project ID");
	    }
		
		return new ResponseEntity<>(projectService.getProjectById(projectId), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Project> createProject(
			@RequestHeader("Authorization") String jwt,
			@RequestBody @Validated Project project
		) throws Exception {
		
	    if (jwt == null || jwt.isEmpty()) {
	        throw new JwtException("JWT token is missing");
	    }
		User user = userService.findUserProfileByJwt(jwt);
		Project createdProject = projectService.createProject(project, user);
		
		return new ResponseEntity<>(createdProject, HttpStatus.OK);
	}
	
	@PatchMapping("/{projectId}")
	public ResponseEntity<Project> updateProject(
			@PathVariable Long projectId,
			@RequestBody Project project
		) throws Exception {
		
	    if (projectId == null || projectId <= 0) {
	        throw new IllegalArgumentException("Invalid project ID");
	    }
		
		Project updatedProject = projectService.updateProject(project, projectId);
		
		return new ResponseEntity<>(updatedProject, HttpStatus.OK);
	}
	
	@DeleteMapping("/{projectId}")
	public ResponseEntity<MessageResponse> deleteProject(
	        @PathVariable Long projectId,
	        @RequestHeader("Authorization") String jwt
	    ) throws Exception {

	    if (projectId == null || projectId <= 0) {
	        throw new IllegalArgumentException("Invalid project ID");
	    }
	    if (jwt == null || jwt.isEmpty()) {
	        throw new JwtException("JWT token is missing");
	    }

	    User user = userService.findUserProfileByJwt(jwt);
	    projectService.deleteProject(projectId, user.getId());

	    MessageResponse res = new MessageResponse();
	    res.setMessage("Project deleted successfully");
	    return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	@GetMapping("/search")
	public ResponseEntity<List<Project>> searchProjects(
			@RequestParam(required = false)String keyword,
			@RequestHeader("Authorization") String jwt
		) throws Exception {
		
	    if (jwt == null || jwt.isEmpty()) {
	        throw new JwtException("JWT token is missing");
	    }
		
		User user = userService.findUserProfileByJwt(jwt);
		List<Project> projects = projectService.searchProjects(keyword, user);
		
		return new ResponseEntity<>(projects, HttpStatus.OK);
	}
	
	@GetMapping("/{projectId}/chat")
	public ResponseEntity<Chat> getChatByProjectId(
			@PathVariable Long projectId
		) throws Exception {
		
	    if (projectId == null || projectId <= 0) {
	        throw new IllegalArgumentException("Invalid project ID");
	    }
		
		return new ResponseEntity<>(projectService.getChatByProjectId(projectId), HttpStatus.OK);
	}
	
	@PostMapping("/invite")
	public ResponseEntity<MessageResponse> inviteProject(
			@RequestBody InviteRequest req,
			@RequestHeader("Authorization") String jwt,
			@RequestBody @Validated Project project
		) throws Exception {
		
	    if (jwt == null || jwt.isEmpty()) {
	        throw new JwtException("JWT token is missing");
	    }
		
		invitationService.sendInvitation(req.getEmail(), req.getProjectId());
		
		MessageResponse res = new MessageResponse();
		res.setMessage("User invitation sent");
		
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	@GetMapping("/accept_invitation")
	public ResponseEntity<Invitation> acceptInviteProject(
			@RequestParam String token,
			@RequestHeader("Authorization") String jwt,
			@RequestBody @Validated Project project
		) throws Exception {
		
	    if (jwt == null || jwt.isEmpty()) {
	        throw new JwtException("JWT token is missing");
	    }
		
		User user = userService.findUserProfileByJwt(jwt);
		Invitation invitation = invitationService.acceptInvitation(token, user.getId());
		projectService.addUserToProject(invitation.getProjectId(), user.getId());
		
		return new ResponseEntity<>(invitation, HttpStatus.ACCEPTED);
	}

}