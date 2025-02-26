package com.yd.projectmanagementsystem.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.yd.projectmanagementsystem.model.PlanType;
import com.yd.projectmanagementsystem.model.Subscription;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.response.PaymentLinkResponse;
import com.yd.projectmanagementsystem.service.PaymentService;
import com.yd.projectmanagementsystem.service.SubscriptionService;
import com.yd.projectmanagementsystem.service.UserService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

	@Autowired
	private UserService userService;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private PaymentService paymentService;

	@PostMapping("/{planType}")
	public ResponseEntity<PaymentLinkResponse> createPaymentLink(@RequestHeader("Authorization") String jwt,
			@PathVariable String planType) throws Exception {

		int amount = 5;
		if (planType.equals("ANNUALLY")) {
			amount *= 12;
			amount = (int) (amount * 0.7);
		}

		try {
			return ResponseEntity.ok(paymentService.createPayment(jwt, amount, planType));

		} catch (PayPalRESTException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new PaymentLinkResponse("Error creating payment link"));
		}
	}

	@GetMapping("/upgrade_plan/success")
	public RedirectView paymentSuccess(@RequestParam("paymentId") String paymentId,
			@RequestParam("PayerID") String payerId, @RequestParam("token") String token,
			@RequestParam("planType") PlanType planType) {

		try {

			return paymentService.createRedirectLink(paymentId, payerId, token, planType);

		} catch (Exception e) {
			e.printStackTrace();
			// Redirect to the frontend error page
			String frontendErrorUrl = "http://localhost:5173/upgrade_plan/error";
			return new RedirectView(frontendErrorUrl);
		}
	}

	@GetMapping("/upgrade_plan/cancel")
	public ResponseEntity<String> paymentCancel() {
		return ResponseEntity.ok("Payment cancelled");
	}

}
