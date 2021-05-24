package com.vgrental.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vgrental.annotations.Authorized;
import com.vgrental.models.Game;
import com.vgrental.models.ROLE;
import com.vgrental.services.GameService;

@RestController
@RequestMapping("/games")
public class GameController {
	
	@Autowired
	private GameService gameService;
	
	@Authorized(allowedRoles = {ROLE.ADMIN})
	@GetMapping
	public ResponseEntity<Set<Game>> findAll() {
		
		Set<Game> allGames = gameService.findAll();
		
		if(allGames.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(allGames);
	}
	
	@Authorized(allowedRoles = {ROLE.ADMIN, ROLE.CUSTOMER})
	@GetMapping(path = "/{gameId}")
	public ResponseEntity<Game> findById(@PathVariable int gameId) {
		return ResponseEntity.ok(gameService.findById(gameId));
	}
	
	@Authorized(allowedRoles = {ROLE.ADMIN, ROLE.CUSTOMER})
	@GetMapping(path = "/search/{name}")
	public ResponseEntity<List<Game>> searchByName(@PathVariable String name) {
		return ResponseEntity.ok(gameService.searchByName(name));
	}
	
	@Authorized(allowedRoles = {ROLE.ADMIN})
	@PostMapping
	public ResponseEntity<Game> insert(@RequestBody Game g) {
		return ResponseEntity.ok(gameService.insert(g));
	}
	
	@Authorized(allowedRoles = {ROLE.ADMIN})
	@DeleteMapping(path = "/{gameId}")
	public ResponseEntity<String> deleteById(@PathVariable int gameId) {
		return ResponseEntity.ok("Game deleted: " + Boolean.toString(gameService.deleteById(gameId)));
	}
}
