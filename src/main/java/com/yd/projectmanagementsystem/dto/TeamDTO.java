package com.yd.projectmanagementsystem.dto;

import java.util.ArrayList;
import java.util.List;

import com.yd.projectmanagementsystem.model.Team;
import com.yd.projectmanagementsystem.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {
	
	private Long id;
	private Long projectId;
	private List<UserDTO> members = new ArrayList<>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public List<UserDTO> getMembers() {
		return members;
	}
	public void setMembers(List<UserDTO> members) {
		this.members = members;
	}
	
	public static TeamDTO toTeamDTO(Team team) {
		TeamDTO dto = new TeamDTO();
		dto.setId(team.getId());
		dto.setProjectId(team.getProject().getId());
		List<UserDTO> membersDTO = new ArrayList<>();
		for(User user:team.getMembers()) {
			membersDTO.add(UserDTO.toUserDTO(user));
		}
		dto.setMembers(membersDTO);
		
		return dto;
	}
	
	

}
