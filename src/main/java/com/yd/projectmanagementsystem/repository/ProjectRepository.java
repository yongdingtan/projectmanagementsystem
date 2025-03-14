package com.yd.projectmanagementsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yd.projectmanagementsystem.model.Project;
import com.yd.projectmanagementsystem.model.Team;
import com.yd.projectmanagementsystem.model.User;

public interface ProjectRepository extends JpaRepository<Project, Long>{
	
	List<Project> findByNameAndOwner(String partialName, User user);
	
	List<Project> findByOwner(User owner);
	
    @Query("SELECT t.members FROM Team t WHERE t.project.id = :projectId")
    List<User> findTeamMembersByProjectId(@Param("projectId") Long projectId);

}