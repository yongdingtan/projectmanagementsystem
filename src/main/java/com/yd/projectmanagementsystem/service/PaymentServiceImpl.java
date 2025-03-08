package com.yd.projectmanagementsystem.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.yd.projectmanagementsystem.model.PlanType;
import com.yd.projectmanagementsystem.model.User;
import com.yd.projectmanagementsystem.response.PaymentLinkResponse;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Value("${paypal.api.merchantid}")
	private String merchantId;

	@Value("${paypal.api.publickey}")
	private String publicKey;

	@Value("${paypal.api.privatekey}")
	private String privateKey;

	private String MODE = "sandbox";

	@Autowired
	private UserService userService;

	@Autowired
	private SubscriptionService subscriptionService;

	@Override
	public PaymentLinkResponse createPayment(String jwt, int amount, String planType) throws Exception {
		// Configure PayPal API context
		APIContext apiContext = new APIContext(merchantId, privateKey, MODE);

		// Build payment details
		Amount paymentAmount = new Amount();
		paymentAmount.setCurrency("SGD");
		paymentAmount.setTotal(String.valueOf(amount));

		Transaction transaction = new Transaction();
		transaction.setAmount(paymentAmount);
		transaction.setDescription("Upgrade to " + planType + " plan");

		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction);

		// Payer information
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");

		// Redirect URLs
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls
				.setCancelUrl("https://projectmanagementsystem-frontend.onrender.com/api/payment/upgrade_plan/cancel");
		redirectUrls.setReturnUrl(
				"https://projectmanagementsystem-frontend.onrender.com/upgrade_plan/success?planType=" + planType); // Include
																													// planType
		// Create payment object
		Payment payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setTransactions(transactions);
		payment.setRedirectUrls(redirectUrls);

		// Execute payment creation
		Payment createdPayment = payment.create(apiContext);

		// Store the paymentId in the user entity
		User user = userService.findUserProfileByJwt(jwt);
		userService.updateUserPaymentId(user, createdPayment.getId()); // Implement this method

		// Extract approval link
		String approvalLink = createdPayment.getLinks().stream().filter(link -> link.getRel().equals("approval_url"))
				.findFirst().map(Links::getHref).orElseThrow(() -> new RuntimeException("Approval URL not found"));

		// Prepare response
		PaymentLinkResponse response = new PaymentLinkResponse("");
		response.setApprovalLink(approvalLink);
		response.setAmount(amount);
		response.setCurrency("SGD");

		return response;
	}

	@Override
	public RedirectView createRedirectLink(String paymentId, String payerId, String token, PlanType planType)
			throws Exception {
		// Execute the payment
		APIContext apiContext = new APIContext(merchantId, privateKey, MODE);
		Payment payment = new Payment();
		payment.setId(paymentId);

		PaymentExecution paymentExecution = new PaymentExecution();
		paymentExecution.setPayerId(payerId);

		Payment executedPayment = payment.execute(apiContext, paymentExecution);

		if (executedPayment.getState().equals("approved")) {
			// Payment was successful
			// Redirect to the frontend success page with planType
			User user = userService.findUserByPaymentId(paymentId);
			if (user != null) {
				subscriptionService.upgradeSubscription(user.getId(), planType);
			}
			String frontendSuccessUrl = "https://projectmanagementsystem-frontend.onrender.com/upgrade_plan/success?planType="
					+ planType;
			return new RedirectView(frontendSuccessUrl);
		} else {
			// Payment failed
			// Redirect to the frontend failure page
			String frontendFailureUrl = "https://projectmanagementsystem-frontend.onrender.com/upgrade_plan/failure";
			return new RedirectView(frontendFailureUrl);
		}
	}

}
