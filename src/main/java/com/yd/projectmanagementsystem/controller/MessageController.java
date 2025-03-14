package com.yd.projectmanagementsystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yd.projectmanagementsystem.model.Chat;
import com.yd.projectmanagementsystem.model.Message;
import com.yd.projectmanagementsystem.request.CreateMessageRequest;
import com.yd.projectmanagementsystem.service.MessageService;
import com.yd.projectmanagementsystem.service.ProjectService;
import com.yd.projectmanagementsystem.service.UserService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
	
	@Autowired(required = false)
	private MessageService messageService;
	
	@Autowired(required = false)
	private UserService userService;
	
	@Autowired(required = false)
	private ProjectService projectService;
	
	
	@PostMapping("/send")
	public ResponseEntity<Message> sendMessage(@RequestBody CreateMessageRequest req) throws Exception {
		
		userService.findUserById(req.getSenderId());
		Chat chat = projectService.getProjectById(req.getProjectId()).getChat();
		if(chat==null) throw new Exception("Chat not found");
		
		Message sentMessage = messageService.sendMessage(req.getSenderId(), req.getProjectId(), req.getContent());
		
		return ResponseEntity.ok(sentMessage);
	}
	
	@GetMapping("/chat/{projectId}")
	public ResponseEntity<List<Message>> getMessagesByChatId(@PathVariable Long projectId) throws Exception {
		return ResponseEntity.ok(messageService.getMessagesByProjectId(projectId));
	}

}
