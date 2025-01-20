package com.yd.projectmanagementsystem.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.api.payments.Payment;
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
	public ResponseEntity<PaymentLinkResponse> createPaymentLink(@RequestHeader("Authorization") String jwt, @PathVariable PlanType planType) throws Exception {
		
		User user = userService.findUserProfileByJwt(jwt);
		int amount = 5;
		if (planType.equals(PlanType.ANNUALLY)) {
		    amount *= 12;
		    amount = (int) (amount * 0.7); 
		}
		
		try {

			APIContext apiContext = new APIContext(merchantId, privateKey, MODE);
			
			Payment requestPayment = new Payment();
			
			JSONObject paymentLinkResponse = new JSONObject();
			paymentLinkResponse.put("amount", amount);
			paymentLinkResponse.put("currency", "SGD");
			
			JSONObject customer = new JSONObject();
			customer.put("name", user.getFullName());
			customer.put("email", user.getEmail());
			paymentLinkResponse.put("customer", customer);
			
			JSONObject notify = new JSONObject();
			notify.put("email", true);
			paymentLinkResponse.put("notify", notify);
			
			paymentLinkResponse.put("callbackUrl", "http://localhost:8080/upgrade_plan/success?planType"+planType);
			Payment approvedPayment = requestPayment.create(apiContext);
			
			
			
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
