package com.yd.projectmanagementsystem.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.yd.projectmanagementsystem.model.PlanType;
import com.yd.projectmanagementsystem.model.User;
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
	public ResponseEntity<PaymentLinkResponse> createPaymentLink(
	        @RequestHeader("Authorization") String jwt, 
	        @PathVariable PlanType planType) throws Exception {

	    User user = userService.findUserProfileByJwt(jwt);
	    int amount = 5;
	    if (planType.equals(PlanType.ANNUALLY)) {
	        amount *= 12;
	        amount = (int) (amount * 0.7);
	    }

	    try {
	        APIContext apiContext = new APIContext(merchantId, privateKey, MODE);

	        Amount paymentAmount = new Amount();
	        paymentAmount.setCurrency("SGD");
	        paymentAmount.setTotal(String.valueOf(amount));

	        Transaction transaction = new Transaction();
	        transaction.setAmount(paymentAmount);
	        transaction.setDescription("Upgrade to " + planType + " plan");

	        List<Transaction> transactions = new ArrayList<>();
	        transactions.add(transaction);

	        Payer payer = new Payer();
	        payer.setPaymentMethod("paypal");

	        RedirectUrls redirectUrls = new RedirectUrls();
	        redirectUrls.setCancelUrl("http://localhost:8080/upgrade_plan/cancel");
	        redirectUrls.setReturnUrl("http://localhost:8080/upgrade_plan/success?planType=" + planType);

	        Payment payment = new Payment();
	        payment.setIntent("sale");
	        payment.setPayer(payer);
	        payment.setTransactions(transactions);
	        payment.setRedirectUrls(redirectUrls);

	        Payment createdPayment = payment.create(apiContext);
	        List<Links> links = createdPayment.getLinks();
	        String approvalLink = links.stream()
	                .filter(link -> "approval_url".equals(link.getRel())) // Ensure the comparison logic is correct
	                .findFirst()
	                .map(Links::getHref) // Ensure the Link class has a getHref() method
	                .orElseThrow(() -> new RuntimeException("Approval URL not found"));


	        PaymentLinkResponse response = new PaymentLinkResponse();
	        response.setApprovalLink(approvalLink);
	        response.setAmount(amount);
	        response.setCurrency("SGD");

	        return ResponseEntity.ok(response);

	    } catch (PayPalRESTException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new PaymentLinkResponse("Error creating payment link"));
	    }
	}


}
