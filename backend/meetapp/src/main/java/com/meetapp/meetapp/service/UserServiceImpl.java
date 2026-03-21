package com.meetapp.meetapp.service;

import com.meetapp.meetapp.dto.LoginRequestDto;
import com.meetapp.meetapp.dto.LoginResponseDto;
import com.meetapp.meetapp.dto.RegisterRequestDto;
import com.meetapp.meetapp.dto.UserResponseDto;
import com.meetapp.meetapp.model.User;
import com.meetapp.meetapp.repository.UserRepository;
import com.meetapp.meetapp.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceImpl implements UserService {
		@Autowired
		private UserRepository userRepository ;
		@Autowired
		private EmailService emailService;
		@Autowired
		private  JwtUtil jwtUtil ;
		@Autowired
		private PasswordEncoder passwordEncoder;

		@Override
		public ResponseEntity<UserResponseDto> registerUser(RegisterRequestDto registerRequestDto) {
				// TODO Auto-generated method stub
				//if email already exists
				if(userRepository.findByEmail(registerRequestDto.getEmail()).isPresent()){
						throw new RuntimeException("Email already exists");
				}

				//save user to database
			  User user = new User();
				user.setName(registerRequestDto.getName());
				user.setEmail(registerRequestDto.getEmail());
				user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword())); // plainText for now


				User savedUser = userRepository.save(user);

				emailService.sendWelcomeEmail(
								savedUser.getEmail(),
								savedUser.getName()
				);
				UserResponseDto response = new UserResponseDto(
								savedUser.getId(),
								savedUser.getName(),
								savedUser.getEmail()
				);
				return ResponseEntity.ok(response);
		}

//		@Override
//		public ResponseEntity<LoginResponseDto> loginUser(LoginRequestDto request){
//				// find user by email
//				User user = userRepository.findByEmail(request.getEmail())
//								.orElseThrow(() -> new LoginResponseDto("Invalid Email Id , ", null);
//				// validate password
//				if(!passwordEncoder.matches(request.getPassword(),user.getPassword())){
//						return new ResponseEntity
//										     .status(HttpStatus.UNAUTHORIZED)
//										     .body(new LoginResponseDto("Invalid credentials", null));
//				}
//
//				//jwt token generation can be added here
//				String token = jwtUtil.generateToken((user.getEmail()));
//
//				return ResponseEntity.ok(
//								new LoginResponseDto("Login successful", token)
//				);
//		}

		// java
		@Override
		public ResponseEntity<LoginResponseDto> loginUser(LoginRequestDto request) {
				// find user by email
				var optionalUser = userRepository.findByEmail(request.getEmail());
				if (optionalUser.isEmpty()) {
						return ResponseEntity
										.status(HttpStatus.UNAUTHORIZED)
										.body(new LoginResponseDto("Invalid Email Id", null));
				}
				User user = optionalUser.get();

				// validate password
				if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
						return ResponseEntity
										.status(HttpStatus.UNAUTHORIZED)
										.body(new LoginResponseDto("Invalid credentials", null));
				}

				// jwt token generation
				String token = jwtUtil.generateToken(user.getEmail());

				return ResponseEntity.ok(new LoginResponseDto("Login successful", token));
		}

}
