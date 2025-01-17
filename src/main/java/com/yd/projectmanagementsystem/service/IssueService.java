package com.yd.projectmanagementsystem.service;

import java.util.List;
import java.util.Optional;

import com.yd.projectmanagementsystem.model.Issue;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.request.IssueRequest;

public interface IssueService {
	
	Issue getIssueById(Long issueId) throws Exception;
	
	List<Issue> getIssuesByProjectId(Long projectId) throws Exception;
	
	Issue createIssue(IssueRequest issue, User user) throws Exception;
	
	Optional<Issue> updateIssue(Long issue, IssueRequest updatedIssue, Long userId) throws Exception;
	
	void deleteIssue(Long issueId, Long userId) throws Exception;
	
	List<Issue> getIssueByAssigneeId(Long assigneeId) throws Exception;
	
	List<Issue> searchIssues(String title, String status, String priority, Long assigneeId) throws Exception;
	
	List<User> getAssigneeForIssue(Long issueId) throws Exception;
	
	Issue addUserToIssue(Long issueId, Long userId) throws Exception;
	
	Issue updateStatus(Long issueId, String status) throws Exception;

}
