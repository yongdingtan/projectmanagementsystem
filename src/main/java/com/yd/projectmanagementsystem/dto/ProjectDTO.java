package com.yd.projectmanagementsystem.dto;

import java.util.ArrayList;
import java.util.List;

import com.yd.projectmanagementsystem.model.Chat;
import com.yd.projectmanagementsystem.model.Project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
	
	private Long id;
	private String name;
	private String description;
	private String category;
	private List<String> tags = new ArrayList<>();
	private Chat chat;
	private Long ownerId;
	private List<IssueDTO> issues = new ArrayList<>();
	private TeamDTO team;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public Chat getChat() {
		return chat;
	}
	public void setChat(Chat chat) {
		this.chat = chat;
	}
	public Long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	public List<IssueDTO> getIssues() {
		return issues;
	}
	public void setIssues(List<IssueDTO> issues) {
		this.issues = issues;
	}
	public TeamDTO getTeam() {
		return team;
	}
	public void setTeam(TeamDTO team) {
		this.team = team;
	}
	
	public static ProjectDTO toProjectDTO(Project project) {
	    ProjectDTO dto = new ProjectDTO();
	    dto.setId(project.getId());
	    dto.setName(project.getName());
	    dto.setDescription(project.getDescription());
	    dto.setCategory(project.getCategory());
	    dto.setTags(project.getTags());
	    dto.setOwnerId(project.getOwner().getId());
	    dto.setTeam(TeamDTO.toTeamDTO(project.getTeam()));
	    return dto;
	}

}
