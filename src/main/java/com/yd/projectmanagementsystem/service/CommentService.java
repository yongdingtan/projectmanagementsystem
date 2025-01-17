package com.yd.projectmanagementsystem.service;

import java.util.List;

import com.yd.projectmanagementsystem.model.Comment;

public interface CommentService {
	
	Comment createComment(Long issueId, Long userId, String comment) throws Exception;
	
	void deleteComment(Long commentId, Long userId) throws Exception;
	
	List<Comment> findCommentByIssueId(Long issueId);

}
