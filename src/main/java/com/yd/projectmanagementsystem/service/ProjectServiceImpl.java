package com.yd.projectmanagementsystem.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.projectmanagementsystem.dto.UserDTO;
import com.yd.projectmanagementsystem.model.Chat;
import com.yd.projectmanagementsystem.model.Project;
import com.yd.projectmanagementsystem.model.Team;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.repository.ProjectRepository;
import com.yd.projectmanagementsystem.repository.TeamRepository;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired(required = false)
	private ProjectRepository projectRepository;

	@Autowired(required = false)
	private UserService userService;

	@Autowired(required = false)
	private ChatService chatService;

	@Autowired(required = false)
	private TeamRepository teamRepository;

	@Override
	public Project createProject(Project project, User user) throws Exception {
		// Create the project and set its fields
		Project createdProject = new Project();
		createdProject.setOwner(user);
		createdProject.setTags(project.getTags());
		createdProject.setName(project.getName());
		createdProject.setCategory(project.getCategory());
		createdProject.setDescription(project.getDescription());

		// Save the project first
		Project savedProject = projectRepository.save(createdProject);

		// Create the team and set its fields
		Team team = new Team();
		List<User> members = new ArrayList<>();
		members.add(user); // Add the owner as the first member
		team.setMembers(members);
		team.setProject(savedProject); // Associate the team with the saved project

		// Save the team
		teamRepository.save(team);

		// Update the project with the team
		savedProject.setTeam(team);
		projectRepository.save(savedProject); // Update the project to reflect the team association

		// Create and associate the chat
		Chat chat = new Chat();
		chat.setProject(savedProject);
		Chat projectChat = chatService.createChat(chat);
		savedProject.setChat(projectChat);

		// Save the updated project with the chat
		return projectRepository.save(savedProject);
	}

	@Override
	public List<Project> getProjectByUserAndCategoryAndTag(User user, String category, String tag) throws Exception {

		// Get the list of projects the user owns
		List<Project> projects = projectRepository.findByOwner(user);

		// Get the list of projects from the teams the user is a part of
		List<Project> projectsFromTeam = userService.getProjectsForUser(user.getId());

		// Combine the two lists
		List<Project> allProjects = new ArrayList<>();
		allProjects.addAll(projects);
		allProjects.addAll(projectsFromTeam);

		// Remove duplicates using a Set with a custom comparator
		allProjects = allProjects.stream().filter(distinctByKey(Project::getId)) // Assuming getId() exists
				.collect(Collectors.toList());

		// Filter by category if provided
		if (category != null) {
			allProjects = allProjects.stream().filter(project -> project.getCategory().equals(category))
					.collect(Collectors.toList());
		}

		// Filter by tag if provided
		if (tag != null) {
			allProjects = allProjects.stream().filter(project -> project.getTags().contains(tag))
					.collect(Collectors.toList());
		}

		return allProjects;
	}

	@Override
	public Project getProjectById(Long projectId) throws Exception {

		Optional<Project> optionalProject = projectRepository.findById(projectId);
		if (optionalProject.isEmpty()) {
			throw new Exception("Project not found");
		}

		return optionalProject.get();
	}

	@Override
	public void deleteProject(Long projectId, Long userId) throws Exception {

		getProjectById(projectId);
		projectRepository.deleteById(projectId);

	}

	@Override
	public Project updateProject(Project updatedProject, Long id) throws Exception {

		Project project = getProjectById(id);

		project.setName(updatedProject.getName());
		project.setDescription(updatedProject.getDescription());
		project.setTags(updatedProject.getTags());
		project.setCategory(updatedProject.getCategory());

		return projectRepository.save(project);
	}

	@Override
	public void addUserToProject(Long projectId, Long userId) throws Exception {

		Project project = getProjectById(projectId);
		User user = userService.findUserById(userId);
		Team team = project.getTeam();
		if (!team.getMembers().contains(user)) {
			project.getChat().getUsers().add(user);
			team.getMembers().add(user);

		}
		teamRepository.save(team);
		projectRepository.save(project);

	}

	@Override
	public void removeUserFromProject(Long projectId, Long userId) throws Exception {

		Project project = getProjectById(projectId);
		User user = userService.findUserById(userId);
		Team team = project.getTeam();
		if (team.getMembers().contains(user)) {
			project.getChat().getUsers().remove(user);
			team.getMembers().remove(user);

		}
		teamRepository.save(team);
		projectRepository.save(project);

	}

	@Override
	public Chat getChatByProjectId(Long projectId) throws Exception {

		return getProjectById(projectId).getChat();
	}

	@Override
	public List<Project> searchProjects(String keyword, User user) throws Exception {

		return projectRepository.findByNameAndOwner(keyword, user);
	}

	public List<User> getTeam(Long projectId) throws Exception {
		return projectRepository.findTeamMembersByProjectId(projectId);
	}
	
	// Helper method
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Set<Object> seen = ConcurrentHashMap.newKeySet();
	    return t -> seen.add(keyExtractor.apply(t));
	}

}