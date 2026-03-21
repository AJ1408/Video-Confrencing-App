package com.meetapp.meetapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {

		@NotBlank(message = "Name cannot be null")
		private String name;

		@NotBlank(message = "Email cannot be null")
		@Email(message = "Email should be valid")
		private String email;

		@NotBlank(message = "Password cannot be null")
		@Size(min = 8 , message = "Password must be at least 8 characters long")
		private String password;
}
