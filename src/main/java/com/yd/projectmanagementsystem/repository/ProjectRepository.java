package com.yd.projectmanagementsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yd.projectmanagementsystem.model.Project;
import com.yd.projectmanagementsystem.model.User;

public interface ProjectRepository extends JpaRepository<Project, Long>{
	
//	List<Project> findByOwner(User user);
	
	List<Project> findByNameAndTeam(String partialName, User user);
	
//	@Query("SELECT p From Project p join p.team t where t=:user")
//	List<Project> findProjectByTeam(@Param("user") User user);
	
	List<Project> findByTeamOrOwner(User user, User owner);

}