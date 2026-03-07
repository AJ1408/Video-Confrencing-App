package com.meetapp.meetapp.service;

import com.meetapp.meetapp.dto.LoginRequestDto;
import com.meetapp.meetapp.dto.LoginResponseDto;
import com.meetapp.meetapp.dto.RegisterRequestDto;
import com.meetapp.meetapp.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
@Service
public interface UserService {
		 ResponseEntity<UserResponseDto> registerUser(RegisterRequestDto registerRequestDto);

		 ResponseEntity<LoginResponseDto> loginUser(LoginRequestDto loginResponseDto);

}