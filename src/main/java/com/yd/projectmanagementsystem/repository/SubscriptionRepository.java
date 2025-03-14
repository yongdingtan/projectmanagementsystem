package com.yd.projectmanagementsystem.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.yd.projectmanagementsystem.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>{
	
	Subscription findByUserId(Long userId);

}
