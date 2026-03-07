package com.meetapp.meetapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
		private String message;
		private String token ;
}
