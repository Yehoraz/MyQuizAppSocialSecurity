package com.MyQuizAppSocialSecurity.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.MyQuizAppSocialSecurity.enums.Roles;
import com.MyQuizAppSocialSecurity.securityBeans.User;
import com.MyQuizAppSocialSecurity.securityBeans.UserRepository;

@RestController
@RequestMapping("/creator/")
@Lazy
public class CreatorController {

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/addAdmin/{username}")
	public ResponseEntity<?> addAdmin(@PathVariable String username) {
		User user = userRepository.findById(username).orElse(null);
		if (user != null) {
			if (!user.getRoles().contains(Roles.ADMIN)) {
				user.addRole(Roles.ADMIN);
				userRepository.save(user);
				return ResponseEntity.status(HttpStatus.OK).body(username + " is now an admin");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(username + " is already an admin");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(username + "don't exists");
		}
	}

	@PutMapping("/removeAdmin/{username}")
	public ResponseEntity<?> removeAdmin(@PathVariable String username) {
		User user = userRepository.findById(username).orElse(null);
		if (user != null) {
			if (user.getRoles().contains(Roles.ADMIN)) {
				user.removeRole(Roles.ADMIN);
				userRepository.save(user);
				return ResponseEntity.status(HttpStatus.OK).body(username + " is not an admin anymore");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(username + " is not an admin");
			}
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(username + " don't exists");
		}
	}

}
