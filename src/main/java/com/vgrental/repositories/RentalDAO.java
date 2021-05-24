package com.vgrental.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vgrental.models.Rental;

public interface RentalDAO extends JpaRepository<Rental, Integer>{

}
