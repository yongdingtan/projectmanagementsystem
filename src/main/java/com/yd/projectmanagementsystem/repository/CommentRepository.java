package com.yd.projectmanagementsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yd.projectmanagementsystem.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{
	
	List<Comment> findCommentByIssueId(Long issueId);

}
