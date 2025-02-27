package com.yd.projectmanagementsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendEmailWithToken(String userEmail, String link) throws MessagingException {
        // Validate input parameters
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalArgumentException("User email cannot be null or empty");
        }
        if (link == null || link.isEmpty()) {
            throw new IllegalArgumentException("Link cannot be null or empty");
        }

        // Create a MimeMessage
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        // Set email details
        String subject = "Join Project Team Invitation";
        String text = "<p>Click the link below to join the project team:</p>"
                    + "<p><a href=\"" + link + "\">Join Project Team</a></p>";

        helper.setSubject(subject);
        helper.setText(text, true); // true indicates HTML content
        helper.setTo(userEmail);

        try {
            // Send the email
            javaMailSender.send(mimeMessage);
            logger.info("Email sent successfully to: {}", userEmail);
        } catch (MailException e) {
            logger.error("Failed to send email to: {}", userEmail, e);
            throw new MailException("Failed to send email: " + e.getMessage(), e) {};
        }
    }
}