package com.vgrental.controllers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vgrental.annotations.Authorized;
import com.vgrental.models.ROLE;
import com.vgrental.models.User;
import com.vgrental.services.AuthorizationService;
import com.vgrental.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController{
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthorizationService authorizationService;
	
	@Authorized(allowedRoles = {ROLE.ADMIN})
	@GetMapping
	public ResponseEntity<Set<User>> findAll() {
		
		Set<User> allUsers = userService.findAll();
		
		if(allUsers.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(allUsers);
	}
	
	@Authorized(allowedRoles = {ROLE.ADMIN, ROLE.CUSTOMER})
	@GetMapping(path = "/{userId}")
	public ResponseEntity<User> findById(@PathVariable int userId) {
		authorizationService.guardByUserId(userId);
		return ResponseEntity.ok(userService.findById(userId));
	}
	
	@PostMapping
	public ResponseEntity<User> insert(@RequestBody User u) {
		// Prevent anyone from being an admin
		if(u.getRole() == ROLE.ADMIN) {
			u.setRole(ROLE.CUSTOMER);
		}
		
		return ResponseEntity.ok(userService.insert(u));
	}
	
	@Authorized(allowedRoles = {ROLE.ADMIN})
	@PostMapping(path = "/admin")
	public ResponseEntity<User> insertAdmin(@RequestBody User u) {
		return ResponseEntity.ok(userService.insert(u));
	}
	
	@Authorized(allowedRoles = {ROLE.ADMIN, ROLE.CUSTOMER})
	@DeleteMapping(path = "/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable int userId) {
		authorizationService.guardByUserId(userId);
		if(userService.deleteById(userId)) {
			return ResponseEntity.ok("User deleted");
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@Authorized(allowedRoles = {ROLE.ADMIN, ROLE.CUSTOMER})
	@PutMapping
	public ResponseEntity<User> update(@RequestBody User u) {
		User beforeUpdate = userService.findById(u.getId());
		authorizationService.guardByUserId(u.getId());
		
		// Prevent anyone from making themselves an admin
		if(beforeUpdate.getRole() == ROLE.CUSTOMER) {
			u.setRole(ROLE.CUSTOMER);
		}
		return ResponseEntity.ok(userService.insert(u));
	}
}
