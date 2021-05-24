package com.vgrental.controllers;

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
import com.vgrental.models.Review;
import com.vgrental.services.GameService;
import com.vgrental.services.ReviewService;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private GameService gameService;
	
	@Authorized(allowedRoles = {ROLE.ADMIN})
	@GetMapping
	public ResponseEntity<Set<Review>> findAll() {
		
		Set<Review> allReviews = reviewService.findAll();
		
		if(allReviews.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(allReviews);
	}
	
	@Authorized(allowedRoles = {ROLE.ADMIN, ROLE.CUSTOMER})
	@PostMapping //(produces = {"application/json"}, consumes = {"application/json;charset"})
	public ResponseEntity<Review> insert(@RequestBody Review r) {
		Review result = reviewService.insert(r); // Insert review
		
		// Update game's average rating
		Game g = gameService.findById(result.getGame().getId());
		g.setAvgRating(reviewService.getAverageRating(g.getId()));
		gameService.insert(g);
		
		return ResponseEntity.ok(result);
	}
	
	@Authorized(allowedRoles = {ROLE.ADMIN})
	@DeleteMapping(path = "/{reviewId}")
	public ResponseEntity<String> delete(@PathVariable int reviewId) {
		Review r = reviewService.findById(reviewId);
		
		// Delete review
		if(reviewService.deleteById(reviewId)) {
			// Update game's average rating
			Game g = gameService.findById(r.getGame().getId());
			g.setAvgRating(reviewService.getAverageRating(g.getId()));
			gameService.insert(g);
			return ResponseEntity.ok("Review successfully deleted");
		}
		
		return ResponseEntity.notFound().build();
	}
}
