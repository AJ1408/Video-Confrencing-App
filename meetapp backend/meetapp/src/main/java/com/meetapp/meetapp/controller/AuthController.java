package com.meetapp.meetapp.controller;

import com.meetapp.meetapp.dto.LoginRequestDto;
import com.meetapp.meetapp.dto.LoginResponseDto;
import com.meetapp.meetapp.dto.RegisterRequestDto;
import com.meetapp.meetapp.dto.UserResponseDto;
import com.meetapp.meetapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/auth")
public class AuthController {
		@Autowired
		UserService userService;

		@PostMapping("/register")
		public ResponseEntity<UserResponseDto> register( @Valid
						@RequestBody RegisterRequestDto registerRequestDto
						) {
				return userService.registerUser(registerRequestDto);
		}

		@PostMapping("login")
		public ResponseEntity<LoginResponseDto> login( @Valid
						@RequestBody LoginRequestDto loginRequestDto
		){

				return userService.loginUser(loginRequestDto);
		}
}
