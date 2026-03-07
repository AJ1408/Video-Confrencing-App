package com.meetapp.meetapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
		@Autowired
		private JavaMailSender mailSender ;
		public void sendWelcomeEmail(String toEmail , String userName){
				SimpleMailMessage message = new SimpleMailMessage();
				message.setTo(toEmail);
				message.setSubject("Welcome to MeetApp");
				message.setText(
								"Hi" +userName + "\n\n" +
												"Welcome to meetApp ! \n" +
												"Your account has been created successfully"
								+  "Happy meeting!\n" +
												"Team MeetApp"
				);
				mailSender.send(message);

		}


}
