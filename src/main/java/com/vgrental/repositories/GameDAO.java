package com.vgrental.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vgrental.models.Game;

public interface GameDAO extends JpaRepository<Game, Integer>{
	public Optional<Game> findByName(String name);
	
	@Query("FROM Game g WHERE g.name LIKE %:name%")
	public List<Game> searchByName(@Param("name") String name);
}
