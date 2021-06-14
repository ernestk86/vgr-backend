package com.vgrental.repositories;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vgrental.models.Rental;
import com.vgrental.models.User;

public interface UserDAO extends JpaRepository<User, Integer>{
	public Optional<User> findByUsername(String username);
}
