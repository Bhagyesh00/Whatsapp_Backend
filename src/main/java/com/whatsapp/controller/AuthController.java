package com.whatsapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whatsapp.config.TokenProvider;
import com.whatsapp.exception.UserException;
import com.whatsapp.model.User;
import com.whatsapp.repository.UserRepository;
import com.whatsapp.request.LoginRequest;
import com.whatsapp.response.AuthResponse;
import com.whatsapp.service.CustomUserService;

@RestController
@RequestMapping("auth")
public class AuthController {
	
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private CustomUserService customUserService;
	
	public AuthController(UserRepository userRepository,PasswordEncoder passwordEncoder,CustomUserService customUserService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.customUserService = customUserService;
	}

	@PostMapping("/signup")
	public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException{

		String full_name = user.getFull_name();
		String email = user.getEmail();
		String password = user.getPassword();
		
		User isUser = userRepository.findByEmail(email);
		if(isUser!=null) {
			throw new UserException("Email is used with another account "+email);
		}
		User createdUser = new User();

		createdUser.setFull_name(full_name);
		createdUser.setEmail(email);
		createdUser.setPassword(passwordEncoder.encode(password));
		
		userRepository.save(createdUser);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(email,password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = new TokenProvider().generateToken(authentication);
		
		AuthResponse res = new AuthResponse(jwt, true);
		return new ResponseEntity<AuthResponse>(res,HttpStatus.ACCEPTED);
	}
	
	public ResponseEntity<AuthResponse> loginHandler(@RequestBody LoginRequest req){
		String email = req.getEmail();
		String password = req.getPassword();
		
		Authentication authentication = authenticate(email,password);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = new TokenProvider().generateToken(authentication);
		
		AuthResponse res = new AuthResponse(jwt, true);
		return new ResponseEntity<AuthResponse>(res,HttpStatus.ACCEPTED);
	}
	
	public Authentication authenticate(String username,String password) {
		UserDetails userDetails = customUserService.loadUserByUsername(username);
		
		if(userDetails==null) {
			throw new BadCredentialsException("Invalid username");
		}
		if(!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("Invalid password");
		}
		
		return new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
	}
}
