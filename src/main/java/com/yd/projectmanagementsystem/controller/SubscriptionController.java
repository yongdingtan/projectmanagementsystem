package com.yd.projectmanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yd.projectmanagementsystem.model.PlanType;
import com.yd.projectmanagementsystem.model.Subscription;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.service.SubscriptionService;
import com.yd.projectmanagementsystem.service.UserService;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {
	
	@Autowired(required = false)
	private SubscriptionService subscriptionService;
	
	@Autowired(required = false)
	private UserService userService;
	
	@GetMapping("/user")
	public ResponseEntity<Subscription> getUserSubscription(@RequestHeader("Authorization") String jwt) throws Exception{
		
		User user = userService.findUserProfileByJwt(jwt);
		Subscription subscription = subscriptionService.getUserSubcription(user.getId());
		
		return new ResponseEntity<>(subscription, HttpStatus.OK);
		
	}
	
	@PatchMapping("/upgrade")
	public ResponseEntity<Subscription> upgradeUserSubscription(@RequestHeader("Authorization") String jwt, @RequestParam PlanType planType) throws Exception{
		
		User user = userService.findUserProfileByJwt(jwt);
		Subscription subscription = subscriptionService.upgradeSubscription(user.getId(), planType);
		
		return new ResponseEntity<>(subscription, HttpStatus.OK);
		
	}
	
	

}
