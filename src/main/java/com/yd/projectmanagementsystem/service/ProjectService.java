package com.yd.projectmanagementsystem.service;

import java.util.List;

import com.yd.projectmanagementsystem.dto.ProjectDTO;
import com.yd.projectmanagementsystem.model.Chat;
import com.yd.projectmanagementsystem.model.Project;
import com.yd.projectmanagementsystem.model.User;

public interface ProjectService {
	
	Project createProject(Project project, User user) throws Exception;
	
	List<Project> getProjectByUserAndCategoryAndTag(User user, String category, String tag) throws Exception;

	Project getProjectById(Long projectId) throws Exception;
	
	void deleteProject(Long projectId, Long userId) throws Exception;
	
	Project updateProject(Project updatedProject, Long id) throws Exception;
	
	void addUserToProject(Long projectId, Long userId) throws Exception;
	
	void removeUserFromProject(Long projectId, Long userId) throws Exception;
	
	Chat getChatByProjectId(Long projectId) throws Exception;
	
	List<Project> searchProjects(String keyword, User user) throws Exception;
	
	List<User> getTeam(Long projectId) throws Exception;

}