package com.nebulak.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.resend.*;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;

@Service
public class EmailService {

	private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @Value("${RESEND_API_KEY}")
    private String accessKey;
    
    private final Resend resend = new Resend(accessKey);

    public void sendActivationEmail(String toEmail, String activationLink) throws ResendException {
       // SimpleMailMessage message = new SimpleMailMessage();
        resend.emails().send(
                SendEmailRequest.builder()
                    .from("dev.karthik.cheekati@gmail.com")
                    .to(toEmail)
                    .subject("Account Activation Required")
                    .html("""
                        <p>Welcome! Thank you for signing up.</p>
                        <p>
                          <a href="%s">
                            Click here to activate your account
                          </a>
                        </p>
                        <p>This link will expire in 24 hours.</p>
                    """.formatted(activationLink))
                    .build()
            );
       // message.setFrom("dev.karthik.cheekati@gmail.com"); // Set your sender email
        //message.setTo(toEmail);
        //message.setSubject("Account Activation Required");
        
       /* String emailContent = String.format(
            "Welcome! Thank you for signing up. Please click the link below to activate your account:\n\n%s\n\nThis link will expire in 24 hours.",
            activationLink
        );*/
        //message.setText(emailContent);
        

       // mailSender.send(message);
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
