package com.nebulak.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationEmail(String toEmail, String activationLink) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        
        message.setFrom("no-reply@yourdomain.com"); // Set your sender email
        message.setTo(toEmail);
        message.setSubject("Account Activation Required");
        
        String emailContent = String.format(
            "Welcome! Thank you for signing up. Please click the link below to activate your account:\n\n%s\n\nThis link will expire in 24 hours.",
            activationLink
        );
        message.setText(emailContent);

        mailSender.send(message);
    }
    public void sendSubscriptionEmail(String toEmail) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        
        message.setFrom("no-reply@yourdomain.com"); // Set your sender email
        message.setTo(toEmail);
        message.setSubject("Ready to Build? Thank You for Subscribing to Nebulak");
        
        String emailContent = String.format(
            "Welcome! Thank you for Subscribing. You’re now officially part of a community focused on building transformative digital products and accelerating developer learning. We are thrilled to have you join our journey."
         
        );
        message.setText(emailContent);

        mailSender.send(message);
    }

	public void sendTicketEmail(String email,String name, Long id) {
		// TODO Auto-generated method stub
		 SimpleMailMessage message = new SimpleMailMessage();
	        
	        message.setFrom("no-reply@yourdomain.com"); // Set your sender email
	        message.setTo(email);
	        message.setSubject(String.format(
		            "Support Ticket Created: TicketId - [ %s ]",
		            id
		        ));
	        
	        String emailContent = String.format(
	            "Hello %s,\n"
	            + "\n"
	            + "Thank you for reaching out to the Nebulak Support Team.\n"
	            + "\n"
	            + "We confirm that your support ticket has been successfully created. We are now reviewing your request and will assign it to the best specialist to help you.",
	            name
	        );
	        message.setText(emailContent);

	        mailSender.send(message);
		
	}
}
