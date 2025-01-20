package com.yd.projectmanagementsystem.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yd.projectmanagementsystem.model.PlanType;
import com.yd.projectmanagementsystem.model.Subscription;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.repository.SubscriptionRepository;

@Service
public class SubscriptionServiceImpl implements SubscriptionService{
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;
	

	@Override
	public Subscription createSubscription(User user) {
		
		Subscription subscription = new Subscription();
		subscription.setUser(user);
		subscription.setSubscriptionStartDate(LocalDate.now());
		subscription.setSubscriptionEndDate(LocalDate.now().plusMonths(12));
		subscription.setValid(true);
		subscription.setPlanType(PlanType.FREE);
		
		return subscriptionRepository.save(subscription);
	}

	@Override
	public Subscription getUserSubcription(Long userId) throws Exception {
		
		Subscription subscription = subscriptionRepository.findByUserId(userId);
		if(!isValid(subscription)) {
			subscription.setPlanType(PlanType.FREE);
			subscription.setSubscriptionStartDate(LocalDate.now());
			subscription.setSubscriptionEndDate(LocalDate.now().plusMonths(12));
		}
		
		return subscriptionRepository.save(subscription);
	}

	@Override
	public Subscription upgradeSubscription(Long userId, PlanType planType) {
		
		Subscription subscription = subscriptionRepository.findByUserId(userId);
		subscription.setPlanType(planType);
		subscription.setSubscriptionStartDate(LocalDate.now());
		
		if(planType.equals(PlanType.MONTHLY)) {
			subscription.setSubscriptionEndDate(LocalDate.now().plusMonths(1));
		} else if(planType.equals(PlanType.ANNUALY)) {
			subscription.setSubscriptionEndDate(LocalDate.now().plusMonths(12));
		}
		
		
		return subscriptionRepository.save(subscription);
	}

	@Override
	public boolean isValid(Subscription subscription) {
		
		if(subscription.getPlanType().equals(PlanType.FREE)) {
			return true;
		}
		
		LocalDate endDate = subscription.getSubscriptionEndDate();
		LocalDate currentDate = LocalDate.now();
		
		return endDate.isAfter(currentDate) || endDate.isEqual(currentDate);
	}

}
