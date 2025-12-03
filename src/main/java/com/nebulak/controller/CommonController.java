package com.nebulak.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nebulak.dto.Contact;
import com.nebulak.dto.Subscribe;
import com.nebulak.service.CommonService;
import com.nebulak.service.EmailService;

@RestController
@RequestMapping("/public")
public class CommonController {
	
	private EmailService emailService;
	private CommonService commonService;
	
	public CommonController(EmailService emailService,CommonService commonService) {
		this.emailService=emailService;
		this.commonService=commonService;
	}
	
	@PostMapping("/subscribe")
	public ResponseEntity<String>Subscribe(@RequestBody Subscribe subscribe){
		try {
			commonService.addSubscription(subscribe.getName(), subscribe.getEmail());
	        emailService.sendSubscriptionEmail(subscribe.getEmail());
	        return ResponseEntity.ok("Thankyou for subscribing to Nebulak");
	        
	    } catch (MailException e) {
            return new ResponseEntity<>("An internal error occurred during role assignment.", HttpStatus.INTERNAL_SERVER_ERROR);

	    }
				
	}
	@PostMapping("/contact")
	public ResponseEntity<String>Contact(@RequestBody Contact contact){
		try {
			Long id = commonService.createTicket(contact.getName(),contact.getMessage(),contact.getEmail());
	        emailService.sendTicketEmail(contact.getEmail(),contact.getName(),id);
	        return ResponseEntity.ok("Thankyou for raising Ticket to Nebulak");
	        
	    } catch (MailException e) {
            return new ResponseEntity<>("An internal error occurred during role assignment.", HttpStatus.INTERNAL_SERVER_ERROR);

	    }
				
	}

}
