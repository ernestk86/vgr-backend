package com.vgrental.services;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.vgrental.models.Game;
import com.vgrental.repositories.GameDAO;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class GameService {
	
	private static final Logger log = LoggerFactory.getLogger(GameService.class);
	private MeterRegistry meterRegistry;
	private Counter failedConnectionAttempts;
	private Counter successfulConnectionAttempts;
	private static final String CONNECTIONATTEMPT = "connection_attempt";
	private static final String TYPE = "type";
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	
	public GameService (MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        successfulConnectionAttempts = meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
		failedConnectionAttempts = meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
    }
	
	@Autowired
	private GameDAO gameDAO;
	
	public Set<Game> findAll() {
		MDC.put("findAll", "games");
		
		Set<Game> allGames = Collections.emptySet();
		
		try {
			allGames = gameDAO.findAll()
				.stream()
				.collect(Collectors.toSet());
			successfulConnectionAttempts.increment(1);
		} catch (DataAccessException e) {
			failedConnectionAttempts.increment(1);
		}
		
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
			try {
				Game savedGame = gameDAO.save(g);
				successfulConnectionAttempts.increment(1);
				return savedGame;
			} catch (DataAccessException e) {
				failedConnectionAttempts.increment(1);
			}
		}
		
		return g;
	}
	
	public Game findById(int gameId) {
		MDC.put("findById", gameId);
		Game g = null;
		
		try {
			g = gameDAO.findById(gameId).orElse(null);
			successfulConnectionAttempts.increment(1);
		} catch (DataAccessException e) {
			failedConnectionAttempts.increment(1);
		}
		
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
			successfulConnectionAttempts.increment(1);
			log.info("Game deleted by DAO");
			MDC.clear();
			return true;
		} catch(DataAccessException e) {
			failedConnectionAttempts.increment(1);
			log.error("Game not found", e);
			return false;
		}
	}
	
	public List<Game> searchByName(String name) {
		MDC.put("searchTerm", name);
		List<Game> searchResults = Collections.emptyList();
		
		try {
			searchResults = gameDAO.searchByName(name);
			successfulConnectionAttempts.increment(1);
		} catch(DataAccessException e) {
			failedConnectionAttempts.increment(1);
		}
		
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
