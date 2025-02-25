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
import com.yd.projectmanagementsystem.response.PaymentLinkResponse;
import com.yd.projectmanagementsystem.service.UserService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

	@Value("${paypal.api.merchantid}")
	private String merchantId;

	@Value("${paypal.api.publickey}")
	private String publicKey;

	@Value("${paypal.api.privatekey}")
	private String privateKey;

	private String MODE = "sandbox";

	@Autowired
	private UserService userService;

	@PostMapping("/{planType}")
	public ResponseEntity<PaymentLinkResponse> createPaymentLink(@RequestHeader("Authorization") String jwt,
			@PathVariable PlanType planType) throws Exception {

		userService.findUserProfileByJwt(jwt);
		int amount = 5;
		if (planType.equals(PlanType.ANNUALLY)) {
			amount *= 12;
			amount = (int) (amount * 0.7);
		}

		try {
			// Debug logs
			System.out.println("Merchant ID: " + merchantId);
			System.out.println("Private Key: " + privateKey);
			System.out.println("Mode: " + MODE);

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
			redirectUrls.setCancelUrl("http://localhost:8080/api/payment/upgrade_plan/cancel");
			redirectUrls.setReturnUrl("http://localhost:8080/api/payment/upgrade_plan/success");
			// redirectUrls.setReturnUrl("http://localhost:8080/upgrade_plan/success?planType="
			// + planType);

			// Create payment object
			Payment payment = new Payment();
			payment.setIntent("sale");
			payment.setPayer(payer);
			payment.setTransactions(transactions);
			payment.setRedirectUrls(redirectUrls);

			// Execute payment creation
			Payment createdPayment = payment.create(apiContext);

			// Extract approval link
			String approvalLink = createdPayment.getLinks().stream()
					.filter(link -> link.getRel().equals("approval_url")).findFirst().map(Links::getHref)
					.orElseThrow(() -> new RuntimeException("Approval URL not found"));

			// Prepare response
			PaymentLinkResponse response = new PaymentLinkResponse();
			response.setApprovalLink(approvalLink);
			response.setAmount(amount);
			response.setCurrency("SGD");

			return ResponseEntity.ok(response);

		} catch (PayPalRESTException e) {
			e.printStackTrace();
			System.err.println("PayPal API Error: " + e.getDetails());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new PaymentLinkResponse("Error creating payment link: " + e.getMessage()));
		}
	}

	@GetMapping("/upgrade_plan/success")
	public RedirectView paymentSuccess(@RequestParam("paymentId") String paymentId,
			@RequestParam("PayerID") String payerId, @RequestParam("token") String token) {

		try {
			// Execute the payment
			APIContext apiContext = new APIContext(merchantId, privateKey, MODE);
			Payment payment = new Payment();
			payment.setId(paymentId);

			PaymentExecution paymentExecution = new PaymentExecution();
			paymentExecution.setPayerId(payerId);

			Payment executedPayment = payment.execute(apiContext, paymentExecution);

			if (executedPayment.getState().equals("approved")) {
				// Payment was successful
				// Redirect to the frontend success page
				String frontendSuccessUrl = "http://localhost:5173/upgrade_plan/success";
				return new RedirectView(frontendSuccessUrl);
			} else {
				// Payment failed
				// Redirect to the frontend failure page
				String frontendFailureUrl = "http://localhost:5173/upgrade_plan/failure";
				return new RedirectView(frontendFailureUrl);
			}

		} catch (PayPalRESTException e) {
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
