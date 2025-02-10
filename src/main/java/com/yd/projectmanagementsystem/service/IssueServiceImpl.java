package com.yd.projectmanagementsystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.projectmanagementsystem.model.Issue;
import com.yd.projectmanagementsystem.model.Project;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.repository.IssueRepository;
import com.yd.projectmanagementsystem.request.IssueRequest;

@Service
public class IssueServiceImpl implements IssueService{
	
	@Autowired
	private IssueRepository issueRepository;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private UserService userService;

	@Override
	public Issue getIssueById(Long issueId) throws Exception {
		
		Optional<Issue> issue = issueRepository.findById(issueId);
		if(issue.isPresent()) {
			return issue.get();
		}
		
		throw new Exception("No issues found with issueId: " + issueId);
	}

	@Override
	public List<Issue> getIssuesByProjectId(Long projectId) throws Exception {
		return issueRepository.findByProjectId(projectId);
	}

	@Override
	public Issue createIssue(IssueRequest issueRequest, User user) throws Exception {
		
		Project project = projectService.getProjectById(issueRequest.getProjectId());
		
		Issue issue = new Issue();
		issue.setTitle(issueRequest.getTitle());
		issue.setDescription(issueRequest.getDescription());
		issue.setStatus(issueRequest.getStatus());
		issue.setPriority(issueRequest.getPriority());
		issue.setCreatedDate(issueRequest.getCreatedDate());
		issue.setDueDate(issueRequest.getDueDate());
		issue.setReporter(user);
		issue.setAssignee(issueRequest.getAssignee());
		issue.setProject(project);
		
		return issueRepository.save(issue);
	}

	@Override
	public Optional<Issue> updateIssue(Long issue, IssueRequest updatedIssue, Long userId) throws Exception {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public void deleteIssue(Long issueId, Long userId) throws Exception {
		getIssueById(issueId);
		issueRepository.deleteById(issueId);;
	}

	@Override
	public List<Issue> getIssueByAssigneeId(Long assigneeId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Issue> searchIssues(String title, String status, String priority, Long assigneeId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getAssigneeForIssue(Long issueId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Issue addUserToIssue(Long issueId, Long userId) throws Exception {
		User user = userService.findUserById(userId);
		Issue issue = getIssueById(issueId);
		issue.setAssignee(user);
		
		return issueRepository.save(issue);
	}

	@Override
	public Issue updateStatus(Long issueId, String status) throws Exception {
		Issue issue = getIssueById(issueId);
		issue.setStatus(status);

		return issueRepository.save(issue);
	}

}
