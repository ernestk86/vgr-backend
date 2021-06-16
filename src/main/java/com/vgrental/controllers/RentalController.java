package com.vgrental.controllers;

import java.time.LocalDate;
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
import com.vgrental.models.Rental;
import com.vgrental.models.User;
import com.vgrental.services.AuthorizationService;
import com.vgrental.services.RentalService;
import com.vgrental.services.UserService;

@RestController
@RequestMapping("/rent")
public class RentalController {

	@Autowired
	private RentalService rentalService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthorizationService authorizationService;
	
	@Authorized(allowedRoles = {ROLE.ADMIN, ROLE.CUSTOMER})
	@GetMapping (path = "/{username}")
	public ResponseEntity<Set<Rental>> getRentals(@PathVariable String username) {
		User u = userService.findByUsername(username);		
		authorizationService.guardByUserId(u.getId());
		return ResponseEntity.ok(userService.getRentals(username));
	}
	
	@Authorized(allowedRoles = {ROLE.ADMIN, ROLE.CUSTOMER})
	@PostMapping (path = "/{userId}/{gameId}")
	public ResponseEntity<Rental> insert(@PathVariable int userId, @PathVariable int gameId) {
		authorizationService.guardByUserId(userId);
		Rental r = rentalService.insert(userId, gameId);
		
		if(r != null) {
			return ResponseEntity.ok(r);
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@Authorized(allowedRoles = {ROLE.ADMIN, ROLE.CUSTOMER})
	@DeleteMapping(path = "/{userId}/{gameId}")
	public ResponseEntity<String> delete(@PathVariable int userId, @PathVariable int gameId) {		
		authorizationService.guardByUserId(userId);
		if(rentalService.deleteById(gameId)) {
			return ResponseEntity.ok("Rental has been returned");
		}
		
		return ResponseEntity.badRequest().build();
	}
	
	@Authorized(allowedRoles = {ROLE.ADMIN})
	@PutMapping(path = "/overdue/{gameId}")
	public ResponseEntity<Rental> overdue(@PathVariable int gameId) {
		Rental r = rentalService.toggleOverdue(gameId);
		
		if(r != null) {
			return ResponseEntity.ok(r);
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@Authorized(allowedRoles = {ROLE.ADMIN})
	@PutMapping(path = "/changeDate/{gameId}")
	public ResponseEntity<Rental> changeDate(@PathVariable int gameId, @RequestBody String date) {
		Rental r = rentalService.changeDate(gameId, LocalDate.parse(date));
		
		if(r != null) {
			return ResponseEntity.ok(r);
		}
		
		return ResponseEntity.notFound().build();
	}
}
