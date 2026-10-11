package com.akhil.ecommerceapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.akhil.ecommerceapi.event.OrderPlacedEvent;

@Service
public class EmailService {
	
	private final JavaMailSender mailSender;
	
	@Value("${spring.mail.username}")
	private String fromAddress;
	
	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	public void sendOrderConfirmation(OrderPlacedEvent event) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromAddress);
		message.setTo(event.getCustomerEmail());
		message.setSubject("Order Confirmation - Order #" + event.getOrderId());
		message.setText(
				"Thank you for your order!\n\n" +
	            "Order ID: " + event.getOrderId() + "\n" +
	            "Total Amount: ₹" + event.getTotalAmount() + "\n\n" +
	            "We're processing your order now."
		);
		mailSender.send(message);
	}

}
