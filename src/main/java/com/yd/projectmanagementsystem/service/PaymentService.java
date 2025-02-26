package com.yd.projectmanagementsystem.service;

import org.springframework.web.servlet.view.RedirectView;

import com.yd.projectmanagementsystem.model.PlanType;
import com.yd.projectmanagementsystem.response.PaymentLinkResponse;

public interface PaymentService {
	
	PaymentLinkResponse createPayment(String jwt, int amount, String planType) throws Exception;
	
	RedirectView createRedirectLink(String paymentId, String payerId, String token, PlanType planType) throws Exception;
}
