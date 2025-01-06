package com.yd.projectmanagementsystem.service;

import org.springframework.stereotype.Service;

import com.yd.projectmanagementsystem.model.Chat;
import com.yd.projectmanagementsystem.repository.ChatRepository;

@Service
public class ChatServiceImpl implements ChatService{

	private ChatRepository chatRepository;
	
	@Override
	public Chat createChat(Chat chat) {
		return chatRepository.save(chat);
	}

}