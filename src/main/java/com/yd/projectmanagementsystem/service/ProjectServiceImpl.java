package com.yd.projectmanagementsystem.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.projectmanagementsystem.model.Chat;
import com.yd.projectmanagementsystem.model.Project;
import com.yd.projectmanagementsystem.model.Team;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.repository.ProjectRepository;
import com.yd.projectmanagementsystem.repository.TeamRepository;
import com.yd.projectmanagementsystem.repository.UserRepository;

@Service
public class ProjectServiceImpl implements ProjectService{
	
	@Autowired
	private ProjectRepository projectRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ChatService chatService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TeamRepository teamRepository;

	@Override
	public Project createProject(Project project, User user) throws Exception {
		
		Project createdProject = new Project();
		createdProject.setOwner(user);
		createdProject.setTags(project.getTags());
		createdProject.setName(project.getName());
		createdProject.setCategory(project.getCategory());
		createdProject.setDescription(project.getDescription());
		Team team = createdProject.getTeam();
		if (team == null) {
		    team = new Team();
		}
		List<User> members = team.getUsers();
		if(members == null) {
			members = new ArrayList<User>();
		}
		members.add(user);
		team.setUsers(members);
		createdProject.setTeam(team);
		teamRepository.save(team);
		
		Project savedProject = projectRepository.save(createdProject);
		user.setProjectSize(user.getProjectSize()+1);
		userRepository.save(user);
		
		Chat chat = new Chat();
		chat.setProject(savedProject);
		
		Chat projectChat = chatService.createChat(chat);
		savedProject.setChat(projectChat);
		
		return savedProject;
	}

	@Override
	public List<Project> getProjectByTeam(User user, String category, String tag) throws Exception {
		
		List<Project> projects = projectRepository.findByOwner(user);
		
		if(category!=null) {
			projects = projects.stream().filter(project -> project.getCategory().equals(category))
					.collect(Collectors.toList());
		}
		
		if(tag!=null) {
			projects = projects.stream().filter(project -> project.getTags().contains(tag))
					.collect(Collectors.toList());
		}
		
		return projects;
	}

	@Override
	public Project getProjectById(Long projectId) throws Exception {
		
		Optional<Project> optionalProject = projectRepository.findById(projectId);
		if(optionalProject.isEmpty()) {
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
		if(!team.getUsers().contains(user)) {
			project.getChat().getUsers().add(user);
			team.getUsers().add(user);
			
		}
		teamRepository.save(team);
		projectRepository.save(project);
		
	}

	@Override
	public void removeUserFromProject(Long projectId, Long userId) throws Exception {

		Project project = getProjectById(projectId);
		User user = userService.findUserById(userId);
		Team team = project.getTeam();
		if(team.getUsers().contains(user)) {
			project.getChat().getUsers().remove(user);
			team.getUsers().remove(user);
			
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

}