package com.vgrental.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vgrental.models.Game;
import com.vgrental.repositories.GameDAO;

@Service
public class GameService {
	
	private static final Logger log = LoggerFactory.getLogger(GameService.class);
	
	@Autowired
	private GameDAO gameDAO;
	
	public Set<Game> findAll() {
		MDC.put("findAll", "games");
		
		Set<Game> allGames = gameDAO.findAll()
				.stream()
				.collect(Collectors.toSet());
		
		if(!allGames.isEmpty()) {
			MDC.put("allGames", allGames);
			log.info("Games retrieved by DAO");
		} else {
			log.error("DAO couldn't retrieve games");
		}
		
		MDC.clear();
		return allGames;
	}
	
	public Game insert(Game g) {
		MDC.put("insert", g);
		
		if(g != null) {
			log.info("Game has been added to DB");
			MDC.clear();
			return gameDAO.save(g);
		}
		
		return g;
	}
	
	public Game findById(int gameId) {
		MDC.put("findById", gameId);
		Game g = gameDAO.findById(gameId).orElse(null);
		
		if(g != null) {
			MDC.put("foundGame", g);
			log.info("DAO was able to find game");
		} else {
			log.error("DAO was unable to find game");
		}
		
		MDC.clear();
		return g;
	}
	
	public boolean deleteById(int gameId) {
		try {
			MDC.put("deleteById", gameId);
			gameDAO.deleteById(gameId);
			log.info("Game deleted by DAO");
			MDC.clear();
			return true;
		} catch(IllegalArgumentException e) {
			log.error("Game not found", e);
			return false;
		}
	}
	
	public List<Game> searchByName(String name) {
		MDC.put("searchTerm", name);
		List<Game> searchResults = gameDAO.searchByName(name);
		
		if(searchResults.isEmpty()) {
			log.error("Search returned nothing");
		} else {
			MDC.put("searchResults", searchResults);
			log.info("Search returned results");
		}
		
		MDC.clear();
		return searchResults;
	}
}
