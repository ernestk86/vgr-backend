package com.vgrental.services;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vgrental.exceptions.UserNotFoundException;
import com.vgrental.models.Rental;
import com.vgrental.models.User;
import com.vgrental.repositories.UserDAO;

@Service
public class UserService {
	
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private HttpServletRequest req;
	
	@Autowired
	private UserDAO userDAO;

	public Set<User> findAll() {
		MDC.put("findAll", "user");
		log.info("Retrieving all users");
		MDC.clear();
		return userDAO.findAll()
				.stream()
				.collect(Collectors.toSet());
	}
	
	public User insert(User u) {
		MDC.put("insert", u);
		log.info("Updating/inserting user");
		MDC.clear();
		return userDAO.save(u);
	}
	
	public Set<Rental> getRentals(String username){
		MDC.put("username", username);
		Optional<User> u = userDAO.findByUsername(username);
		
		if (u.isPresent()) {
			log.info("Retrieving user's rentals list");
			MDC.clear();
			return u.get().getRentals();
		}
		
		log.error("Unable to retrieve user's rentals list");
		MDC.clear();
		return Collections.emptySet();
	}
	
	public User findByUsername(String username) {
		MDC.put("username", username);
		Optional<User> u = userDAO.findByUsername(username);
		
		if(u.isPresent()) {
			log.info("Found user");
			MDC.clear();
			return u.get();
		}
		
		log.error("Unable to find user");
		MDC.clear();
		return null;
	}
	
	public User findById(int userId) {
		MDC.put("userId", Integer.toString(userId));
		User u = userDAO.findById(userId).orElse(null);
		
		if(u != null) {
			log.info("User found");
		} else {
			log.error("Unable to find user");
		}
		
		MDC.clear();
		return u;
	}
	
	public boolean deleteById(int userId) {
		MDC.put("userId", Integer.toString(userId));
		Optional<User> u = userDAO.findById(userId);
		
		if(u.isPresent()) {
			try {
				userDAO.deleteById(userId);
				log.info("User deleted from DB");
				MDC.clear();
				return true;
			} catch(IllegalArgumentException e) {
				log.error("User does not exist", e);
				MDC.clear();
				return false;
			}
		}
		
		log.error("User does not exist");
		MDC.clear();
		return false;
	}
	
	public User login(String username, String password) {
		MDC.put("login", username);
		User u = userDAO.findByUsername(username)
							.orElseThrow(() -> new UserNotFoundException(String.format("No User with username = %s", username)));
		
		if(u.getPassword().equals(password)) {
			HttpSession session = req.getSession();
			session.setAttribute("currentUser", u);
			log.info("User successfully logged in");
			MDC.clear();
			return u;
		} 
		
		MDC.put("wrongPassword", password);
		log.error("Incorrect password");
		MDC.clear();
		return null;
	}
	
	public void logout() {
		
		HttpSession session = req.getSession(false);
		
		if(session == null) {
			// No one was logged in
			
			return;
		}
		
		session.invalidate();
		log.info("Logged out");
		MDC.clear();
	}
}
