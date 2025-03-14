package com.yd.projectmanagementsystem.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.springframework.web.servlet.view.RedirectView;

import com.yd.projectmanagementsystem.config.JwtProvider;
import com.yd.projectmanagementsystem.dto.ProjectDTO;
import com.yd.projectmanagementsystem.model.Chat;
import com.yd.projectmanagementsystem.model.Invitation;
import com.yd.projectmanagementsystem.model.Project;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.request.InviteRequest;
import com.yd.projectmanagementsystem.response.MessageResponse;
import com.yd.projectmanagementsystem.service.InvitationService;
import com.yd.projectmanagementsystem.service.ProjectService;
import com.yd.projectmanagementsystem.service.UserService;
import com.yd.projectmanagementsystem.util.ProjectMapper;

import io.jsonwebtoken.JwtException;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

	@Autowired(required = false)
    private ProjectService projectService;

    @Autowired(required = false)
    private UserService userService;

    @Autowired(required = false)
    private InvitationService invitationService;

    // Get all projects with optional filters
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getProjects(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestHeader("Authorization") String jwt) throws Exception {

        if (jwt == null || jwt.isEmpty()) {
            throw new JwtException("JWT token is missing");
        }

        User user = userService.findUserProfileByJwt(jwt);
        List<Project> projects = projectService.getProjectByUserAndCategoryAndTag(user, category, tag);

        // Convert Project entities to ProjectDTOs
        List<ProjectDTO> projectDTOs = projects.stream()
                .map(ProjectMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(projectDTOs, HttpStatus.OK);
    }

    // Get a project by ID
    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long projectId) throws Exception {

        if (projectId == null || projectId <= 0) {
            throw new IllegalArgumentException("Invalid project ID");
        }

        Project project = projectService.getProjectById(projectId);

        return new ResponseEntity<>(project, HttpStatus.OK);
    }

    // Create a new project
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(
            @RequestHeader("Authorization") String jwt,
            @RequestBody @Validated Project project) throws Exception {

        if (jwt == null || jwt.isEmpty()) {
            throw new JwtException("JWT token is missing");
        }

        User user = userService.findUserProfileByJwt(jwt);
        Project createdProject = projectService.createProject(project, user);

        // Convert the created Project entity to a ProjectDTO
        ProjectDTO projectDTO = ProjectMapper.toDTO(createdProject);

        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
    }

    // Update a project
    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long projectId,
            @RequestBody Project project) throws Exception {

        if (projectId == null || projectId <= 0) {
            throw new IllegalArgumentException("Invalid project ID");
        }

        Project updatedProject = projectService.updateProject(project, projectId);

        // Convert the updated Project entity to a ProjectDTO
        ProjectDTO projectDTO = ProjectMapper.toDTO(updatedProject);

        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
    }

    // Delete a project
    @DeleteMapping("/{projectId}")
    public ResponseEntity<MessageResponse> deleteProject(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String jwt) throws Exception {

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

    // Search projects
    @GetMapping("/search")
    public ResponseEntity<List<ProjectDTO>> searchProjects(
            @RequestParam(required = false) String keyword,
            @RequestHeader("Authorization") String jwt) throws Exception {

        if (jwt == null || jwt.isEmpty()) {
            throw new JwtException("JWT token is missing");
        }

        User user = userService.findUserProfileByJwt(jwt);
        List<Project> projects = projectService.searchProjects(keyword, user);

        // Convert Project entities to ProjectDTOs
        List<ProjectDTO> projectDTOs = projects.stream()
                .map(ProjectMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(projectDTOs, HttpStatus.OK);
    }

    // Other endpoints (unchanged)
    @GetMapping("/{projectId}/chat")
    public ResponseEntity<Chat> getChatByProjectId(@PathVariable Long projectId) throws Exception {
        if (projectId == null || projectId <= 0) {
            throw new IllegalArgumentException("Invalid project ID");
        }
        return new ResponseEntity<>(projectService.getChatByProjectId(projectId), HttpStatus.OK);
    }

    @PostMapping("/invite")
    public ResponseEntity<MessageResponse> inviteProject(
            @RequestBody InviteRequest req,
            @RequestHeader("Authorization") String jwt) throws Exception {

        if (jwt == null || jwt.isEmpty()) {
            throw new JwtException("JWT token is missing");
        }

        String token = jwt.replace("Bearer ", "");
        if (!JwtProvider.validateToken(token)) {
            throw new JwtException("Invalid JWT token");
        }

        invitationService.sendInvitation(req.getEmail(), req.getProjectId());

        MessageResponse res = new MessageResponse();
        res.setMessage("User invitation sent");
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/accept_invitation")
    public ResponseEntity<Map<String, String>> acceptInviteProject(
            @RequestParam String token,
            @RequestHeader(value = "Authorization", required = false) String jwt) throws Exception {

        User user = null;

        // If JWT is provided, verify user
        if (jwt != null && !jwt.isEmpty()) {
            user = userService.findUserProfileByJwt(jwt);
        }

        // If user is null, return a response indicating the user needs to log in
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("redirect", "/login?redirect=/accept-invitation?token=" + token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Accept invitation and add user to project
        Invitation invitation = invitationService.acceptInvitation(token, user.getId());
        projectService.addUserToProject(invitation.getProjectId(), user.getId());

        // Return the project ID to the frontend
        Map<String, String> response = new HashMap<>();
        response.put("projectId", invitation.getProjectId().toString());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{projectId}/team")
    public ResponseEntity<List<User>> getTeamByProjectId(@PathVariable Long projectId) throws Exception {
        return new ResponseEntity<>(projectService.getTeam(projectId), HttpStatus.OK);
    }
}