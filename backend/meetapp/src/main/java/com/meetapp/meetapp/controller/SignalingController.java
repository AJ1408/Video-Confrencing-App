package com.meetapp.meetapp.controller;

import com.meetapp.meetapp.model.SignalMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class SignalingController {

		// Logic:
		// 1. React sends message to: /app/room/{roomId}
		// 2. We receive it here.
		// 3. We return it, and it automatically goes to: /topic/room/{roomId}

		@MessageMapping("/room/{roomId}")
		@SendTo("/topic/room/{roomId}")
		public SignalMessage handleMessage(@DestinationVariable String roomId , @Payload SignalMessage message){
				System.out.println("Received message for room: " + roomId + " , Signal Type: " + message.getType() + " , from: " + message.getSender());
		   return message ;
		}
}
