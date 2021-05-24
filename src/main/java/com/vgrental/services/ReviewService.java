package com.vgrental.services;

import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vgrental.models.Review;
import com.vgrental.repositories.ReviewDAO;

@Service
public class ReviewService {
	
	private static final Logger log = LoggerFactory.getLogger(ReviewService.class);
	
	@Autowired
	private ReviewDAO reviewDAO;
	
	public Set<Review> findAll() {
		MDC.put("findAll", "review");
		log.info("Retrieving all reviews");
		MDC.clear();
		return reviewDAO.findAll()
				.stream()
				.collect(Collectors.toSet());
	}
	
	public Review insert(Review r) {
		MDC.put("insert", r);
		log.info("Updating review");
		MDC.clear();
		return reviewDAO.save(r);
	}
	
	public boolean deleteById(int reviewId) {
		MDC.put("reviewId", Integer.toString(reviewId));
		try {
			reviewDAO.deleteById(reviewId);
			log.info("Review has been deleted");
			MDC.clear();
			return true;
		} catch(IllegalArgumentException e) {
			log.error("Review unable to be deleted", e);
			MDC.clear();
			return false;
		}
	}
	
	public double getAverageRating(int gameId) {
		MDC.put("gameId", Integer.toString(gameId));
		log.info("Average score retrieved");
		MDC.clear();
		return reviewDAO.getAverageRating(gameId);
	}
	
	public Review findById(int id) {
		MDC.put("reviewId", Integer.toString(id));
		Review r = reviewDAO.findById(id).orElse(null);
		
		if(r != null) {
			log.info("Average score retrieved");
		} else {
			log.error("Couldn't retrieve average score");
		}
		
		MDC.clear();
		return r;
	}
}
