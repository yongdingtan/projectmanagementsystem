package com.yd.projectmanagementsystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yd.projectmanagementsystem.dto.IssueDTO;
import com.yd.projectmanagementsystem.model.Issue;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.request.IssueRequest;
import com.yd.projectmanagementsystem.response.AuthResponse;
import com.yd.projectmanagementsystem.service.IssueService;
import com.yd.projectmanagementsystem.service.UserService;

@RestController
@RequestMapping("/api/issue")
public class IssueController {
	
	@Autowired(required = false)
	private IssueService issueService;
	
	@Autowired(required = false)
	private UserService userService;
	
	@GetMapping("/{issueId}")
	public ResponseEntity<Issue> getIssueById(@PathVariable Long issueId) throws Exception {
		return ResponseEntity.ok(issueService.getIssueById(issueId));
	}
	
	@GetMapping("/project/{projectId}")
	public ResponseEntity<List<Issue>> getIssueByProjectId(@PathVariable Long projectId) throws Exception {
		return ResponseEntity.ok(issueService.getIssuesByProjectId(projectId));
	}
	
	@PostMapping
	public ResponseEntity<IssueDTO> createIssue(@RequestBody IssueRequest issue, @RequestHeader("Authorization") String token) throws Exception {
		User tokenUser = userService.findUserProfileByJwt(token);
		User user = userService.findUserById(tokenUser.getId());
		
		Issue createdIssue = issueService.createIssue(issue, user);
		IssueDTO issueDTO = new IssueDTO();
		issueDTO.setDescription(createdIssue.getDescription());
		issueDTO.setCreatedDate(createdIssue.getCreatedDate());
		issueDTO.setDueDate(createdIssue.getDueDate());
		issueDTO.setId(createdIssue.getId());
		issueDTO.setPriority(createdIssue.getPriority());
		issueDTO.setProject(createdIssue.getProject());
		issueDTO.setStatus(createdIssue.getStatus());
		issueDTO.setTitle(createdIssue.getTitle());
		issueDTO.setTags(createdIssue.getTags());
		issueDTO.setReporter(createdIssue.getReporter());
		issueDTO.setAssignee(createdIssue.getAssignee());
		
		
		return ResponseEntity.ok(issueDTO);
	}
	
	@DeleteMapping("/{issueId}")
	public ResponseEntity<AuthResponse> deleteIssue(@PathVariable Long issueId, @RequestHeader("Authorization") String token) throws Exception {
		User user = userService.findUserProfileByJwt(token);
		issueService.deleteIssue(issueId, user.getId());
		
		AuthResponse res = new AuthResponse();
		res.setMessage("Issue deleted");
		res.setStatus(true);
		
		return ResponseEntity.ok(res);
	}
	
	@PutMapping("/{issueId}/assignee/{userId}")
	public ResponseEntity<Issue> addUserToIssue(@PathVariable Long issueId, @PathVariable Long userId) throws Exception {
		return ResponseEntity.ok(issueService.addUserToIssue(issueId, userId));
	}
	
	@PutMapping("/{issueId}/status/{status}")
	public ResponseEntity<Issue> updateIssueStatus(@PathVariable String status, @PathVariable Long issueId) throws Exception {
		return ResponseEntity.ok(issueService.updateStatus(issueId, status));
	}
	
	

}
