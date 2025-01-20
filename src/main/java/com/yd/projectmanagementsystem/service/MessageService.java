package com.yd.projectmanagementsystem.service;

import java.util.List;

import com.yd.projectmanagementsystem.model.Message;

public interface MessageService {
	
	Message sendMessage(Long senderId, Long projectId, String content) throws Exception;
	
	List<Message> getMessagesByProjectId(Long projectId) throws Exception;

}
