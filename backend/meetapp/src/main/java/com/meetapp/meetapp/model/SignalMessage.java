package com.meetapp.meetapp.model;

import lombok.Data;

@Data
public class SignalMessage {
		private String type; // offer, answer, candidate join
		private String sender ; //userId or name
		private Object data; //The complex webRtc data (offer, answer, candidate)
		private String roomId; // The room to which this message belongs
		private String fromUserId ; // The userId of the sender
		private String toUserId ; // The userId of the recipient (for private messages)
		private String eventType ; // The type of event (e.g., "offer", "answer", "candidate", "join", "leave")
		private String timestamp; // The time when the message was sent
		private String correlationId; // A unique identifier to correlate messages in the same session

		
}
