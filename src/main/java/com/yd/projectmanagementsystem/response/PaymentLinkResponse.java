package com.yd.projectmanagementsystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLinkResponse {
	
    private String approvalLink;
    private int amount;
    private String currency;
    private String message;

    public PaymentLinkResponse(String message) {
        this.message = message;
    }

    // Getters and setters
    public String getApprovalLink() {
        return approvalLink;
    }

    public void setApprovalLink(String approvalLink) {
        this.approvalLink = approvalLink;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

