package com.meetapp.meetapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("meet")
public class MeetController {

		@GetMapping("/test")
		public String testMeet(){
				return "Meet Service is working";
		}
}
