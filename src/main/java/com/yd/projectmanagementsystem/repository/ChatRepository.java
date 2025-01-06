package com.yd.projectmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yd.projectmanagementsystem.model.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long>{

}