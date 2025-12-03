package com.nebulak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nebulak.model.EmailSubscriptions;
import com.nebulak.model.Tickets;
import com.nebulak.repository.EmailSubscriptionsRepository;
import com.nebulak.repository.TicketCreationRepository;

@Service
public class CommonService {
	@Autowired
    private EmailSubscriptionsRepository emailSubscriptionsRepository;
	
	@Autowired
    private TicketCreationRepository ticketCreationRepository;
	
	public String addSubscription(String name, String email) {
		EmailSubscriptions subscription = new EmailSubscriptions();
		subscription.setName(name);
		subscription.setEmail(email);		
		emailSubscriptionsRepository.save(subscription);
		return "Successfully Subscribed";
	}

	public Long createTicket(String name, String message, String email) {
		// TODO Auto-generated method stub
		Tickets ticket=new Tickets();
		ticket.setName(name);
		ticket.setEmail(email);
		ticket.setMessage(message);
		ticketCreationRepository.save(ticket);
		
		
		return ticket.getId();
		
	}

}
