package com.yd.projectmanagementsystem.service;

import com.yd.projectmanagementsystem.model.PlanType;
import com.yd.projectmanagementsystem.model.Subscription;
import com.yd.projectmanagementsystem.model.User;

public interface SubscriptionService {
	
	Subscription createSubscription(User user);
	
	Subscription getUserSubcription(Long userId) throws Exception;
	
	Subscription upgradeSubscription(Long userId, PlanType planType);
	
	boolean isValid(Subscription subscription);

}
