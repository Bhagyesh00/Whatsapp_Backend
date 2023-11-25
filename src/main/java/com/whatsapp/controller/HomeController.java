package com.whatsapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
	
	public HomeController() {
		super();
	}

	@GetMapping("/")
	public ResponseEntity<String> homeController(){
		return new ResponseEntity<String>("Welcome to our HomePage",HttpStatus.OK);
	}
	
}
