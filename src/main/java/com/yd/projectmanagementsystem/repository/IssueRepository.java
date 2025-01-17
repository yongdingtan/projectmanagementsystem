package com.yd.projectmanagementsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yd.projectmanagementsystem.model.Issue;

public interface IssueRepository extends JpaRepository<Issue, Long>{
	
	public List<Issue> findByProjectId(Long projectId);

}
