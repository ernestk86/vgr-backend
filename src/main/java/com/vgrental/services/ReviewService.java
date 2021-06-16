package com.vgrental.services;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.vgrental.models.Review;
import com.vgrental.repositories.ReviewDAO;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class ReviewService {
	
	private static final Logger log = LoggerFactory.getLogger(ReviewService.class);
	private MeterRegistry meterRegistry;
	private Counter failedConnectionAttempts;
	private Counter successfulConnectionAttempts;
	private static final String CONNECTIONATTEMPT = "connection_attempt";
	private static final String TYPE = "type";
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	
	public ReviewService (MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        successfulConnectionAttempts = meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
		failedConnectionAttempts = meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
    }
	
	@Autowired
	private ReviewDAO reviewDAO;
	
	public Set<Review> findAll() {
		MDC.put("findAll", "review");
		log.info("Retrieving all reviews");
		MDC.clear();
		
		Set<Review> allReviews = Collections.emptySet();
		
		try {
			allReviews = reviewDAO.findAll()
				.stream()
				.collect(Collectors.toSet());
			successfulConnectionAttempts.increment(1);
		} catch (DataAccessException e) {
			failedConnectionAttempts.increment(1);
		}
		
		return allReviews;
	}
	
	public Review insert(Review r) {
		MDC.put("insert", r);
		log.info("Updating review");
		MDC.clear();
		
		Review newReview = null;
		
		try {
			newReview = reviewDAO.save(r);
			successfulConnectionAttempts.increment(1);
		} catch (DataAccessException e) {
			failedConnectionAttempts.increment(1);
		}
		
		return newReview;
	}
	
	public boolean deleteById(int reviewId) {
		MDC.put("reviewId", Integer.toString(reviewId));
		try {
			reviewDAO.deleteById(reviewId);
			successfulConnectionAttempts.increment(1);
			log.info("Review has been deleted");
			MDC.clear();
			return true;
		} catch(DataAccessException e) {
			log.error("Review unable to be deleted", e);
			MDC.clear();
			failedConnectionAttempts.increment(1);
			return false;
		}
	}
	
	public double getAverageRating(int gameId) {
		MDC.put("gameId", Integer.toString(gameId));
		log.info("Average score retrieved");
		MDC.clear();
		
		Double averageRating = 0.0;
		
		try {
			averageRating = reviewDAO.getAverageRating(gameId);
			successfulConnectionAttempts.increment(1);
		} catch (DataAccessException e) {
			failedConnectionAttempts.increment(1);
		}
		
		return averageRating;
	}
	
	public Review findById(int id) {
		MDC.put("reviewId", Integer.toString(id));
		Review r = null;
		
		try {
			r = reviewDAO.findById(id).orElse(null);
			successfulConnectionAttempts.increment(1);
		} catch (DataAccessException e) {
			failedConnectionAttempts.increment(1);
		}
		
		if(r != null) {
			log.info("Average score retrieved");
		} else {
			log.error("Couldn't retrieve average score");
		}
		
		MDC.clear();
		return r;
	}
}
