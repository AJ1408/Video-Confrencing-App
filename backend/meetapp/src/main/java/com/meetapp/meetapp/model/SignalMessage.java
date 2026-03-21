package com.meetapp.meetapp.model;

import lombok.Data;

@Data
public class SignalMessage {
		private String type; // offer, answer, candidate join
		private String sender ; //userId or name
		private Object data; //The complex webRtc data (offer, answer, candidate)
}
