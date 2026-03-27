package com.meetapp.meetapp.controller;

import com.meetapp.meetapp.model.SignalMessage;

import java.util.UUID;

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
	public SignalMessage handleMessage(@DestinationVariable String roomId,
			@Payload SignalMessage message) {
		if (message == null) {
			System.out.println("Received null message for room: " + roomId);
			return null;
		}
		if (isBlank(message.getRoomId())) {
			System.out.println("Received message without roomId, setting it to: " + roomId);
			message.setRoomId(roomId);
		}
		if (isBlank(message.getFromUserId()) && !isBlank(message.getSender())) {
			System.out.println("Received message without fromUserId, setting it to sender: " + message.getSender());
			message.setFromUserId(message.getSender());
		}
		if (isBlank(message.getEventType()) && !isBlank(message.getType())) {
			message.setEventType(message.getType());
		}

		if (isBlank(message.getSender()) && !isBlank(message.getFromUserId())) {
			message.setSender(message.getFromUserId());
		}
		if (isBlank(message.getType()) && !isBlank(message.getEventType())) {
			message.setType(message.getEventType());
		}

		if (isBlank(message.getTimestamp())) {
			message.setTimestamp(String.valueOf(System.currentTimeMillis()));
		}

		if (isBlank(message.getCorrelationId())) {
			message.setCorrelationId(UUID.randomUUID().toString());
		}

		System.out.println(
				"Received message room=" + message.getRoomId()
						+ ", event=" + message.getEventType()
						+ ", from=" + message.getFromUserId()
						+ ", to=" + message.getToUserId()
						+ ", correlationId=" + message.getCorrelationId());

		return message;
	}

	private boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
	}
}
