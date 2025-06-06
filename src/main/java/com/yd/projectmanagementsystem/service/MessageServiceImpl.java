package com.yd.projectmanagementsystem.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.projectmanagementsystem.model.Chat;
import com.yd.projectmanagementsystem.model.Message;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.repository.MessageRepository;
import com.yd.projectmanagementsystem.repository.UserRepository;

@Service
public class MessageServiceImpl implements MessageService{
	
	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ProjectService projectService;

	@Override
	public Message sendMessage(Long senderId, Long projectId, String content) throws Exception {
		
		User sender = userRepository.findById(senderId).orElseThrow(() -> new Exception("User not found with id: " + senderId));
		Chat chat = projectService.getProjectById(projectId).getChat();
		
		Message message = new Message();
		message.setContent(content);
		message.setSender(sender);
		message.setCreatedAt(LocalDateTime.now());
		message.setChat(chat);
		Message savedMessage = messageRepository.save(message);
		
		chat.getMessages().add(savedMessage);
		
		return savedMessage;
	}

	@Override
	public List<Message> getMessagesByProjectId(Long projectId) throws Exception {
		
		Chat chat = projectService.getChatByProjectId(projectId);
		List<Message> findByChatIdOrderByCreatedAtAsc = messageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId());
		
		return findByChatIdOrderByCreatedAtAsc;
	}
	
	@Override
	public void deleteMessageById(Long messageId) throws Exception {
	    Message message = messageRepository.findById(messageId)
	        .orElseThrow(() -> new Exception("Message not found"));
	    Chat chat = message.getChat();
	    chat.getMessages().remove(message); // Ensure orphanRemoval takes effect
	    messageRepository.delete(message);
	}

}
