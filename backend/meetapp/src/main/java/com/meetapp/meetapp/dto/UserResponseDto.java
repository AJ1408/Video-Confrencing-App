package com.meetapp.meetapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDto {
		private Long id;
		private String name;
		private String email;
}
