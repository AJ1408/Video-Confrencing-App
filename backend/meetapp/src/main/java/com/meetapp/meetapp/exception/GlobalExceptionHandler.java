// java
package com.meetapp.meetapp.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

		@ExceptionHandler(MethodArgumentNotValidException.class)
		public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
				Map<String, String> errors = new HashMap<>();
				for (FieldError error : ex.getBindingResult().getFieldErrors()) {
						errors.put(error.getField(), error.getDefaultMessage());
				}
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}

		@ExceptionHandler(ConstraintViolationException.class)
		public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
				Map<String, String> errors = new HashMap<>();
				ex.getConstraintViolations().forEach(cv -> {
						String path = cv.getPropertyPath().toString();
						errors.put(path, cv.getMessage());
				});
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}

		@ExceptionHandler(Exception.class)
		public ResponseEntity<Map<String, String>> handleOtherExceptions(Exception ex) {
				Map<String, String> body = new HashMap<>();
				body.put("error", ex.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
		}
}