package com.nebulak.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;


@Service
public class EmailService {

	
	@Value("${sendgrid.api-key}")
	private String sendGridApiKey;
	
	// Inject the verified FROM email
	@Value("${sendgrid.from-email}")
	private String fromEmail;
	private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
private void sendMail(String toEmail, String subject, String emailContent) {
    	
    	Email from = new Email(fromEmail); // Uses the verified email from application.properties
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", emailContent);
        Mail mail = new Mail(from, subject, to, content);
        
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        
        try {
        	request.setMethod(Method.POST);
        	request.setEndpoint("mail/send");
        	request.setBody(mail.build());
        	Response response = sg.api(request);
        	
        	// Log success or failure based on the HTTP response code
        	if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
        		System.out.println("SendGrid API Email Sent Status: " + response.getStatusCode());
        	} else {
        		System.err.println("SendGrid API Email Failed Status: " + response.getStatusCode());
        		System.err.println("SendGrid API Response Body: " + response.getBody());
        		// You could throw an exception here if the failure is critical
        	}
        	
        } catch (IOException ex) {
        	System.err.println("IOException during SendGrid API call: " + ex.getMessage());
        	// Throw a runtime exception to handle the failure
        	throw new RuntimeException("Failed to send email via SendGrid API.", ex);
        }
    }
public void sendActivationEmail(String toEmail, String activationLink){
	
    String subject = "Account Activation Required";
     
    String emailContent = String.format(
         "Welcome! Thank you for signing up. Please click the link below to activate your account:\n\n%s\n\nThis link will expire in 24 hours.",
         activationLink
     );
     
    sendMail(toEmail, subject, emailContent);
 }
    /*public void sendActivationEmail(String toEmail, String activationLink){
       SimpleMailMessage message = new SimpleMailMessage();
    	
       message.setFrom("dev.karthik.cheekati@gmail.com"); // Set your sender email
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
*/
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
