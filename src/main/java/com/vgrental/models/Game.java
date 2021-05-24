package com.vgrental.models;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "games", schema = "rentals_vg")
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(exclude = {"rental"}) @ToString(exclude = {"rental"})
public class Game {

	@Id
	@Column(nullable = false, unique = true, updatable = false)
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	//@OneToMany(cascade = CascadeType.ALL, targetEntity = Review.class)
	//@JsonManagedReference("label1")
	private int id;
	
	@Column(nullable = false) // MUST SET COLUMN AS UNIQUE IN DB
	@Length (min = 1)
	@NotBlank
	private String name;
	
	@Column(nullable = false)
	@Enumerated (EnumType.STRING)
	private GENRE genre;
	
	@Column(nullable = false)
	@Enumerated (EnumType.STRING)
	private CONSOLES console;
	
	private String publisher;
	
	private String developer;
	
	@Basic
	@JsonFormat (pattern = "yyyy-MM-dd")
	@Past
	private LocalDate dateReleased;
	
	@Column(columnDefinition = "boolean default false")
	private boolean multiplayer;
	
	@Column(columnDefinition = "boolean default true")
	private boolean available = true;
	
	@Column(columnDefinition = "decimal default 0.0")
	private double avgRating;
	
	@JsonManagedReference("GameReview")
	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
	private Set<Review> reviews;
	
	@JsonManagedReference("GameRental")
	@OneToOne(mappedBy = "game")
	private Rental rental;
	
	public Game(int id) {
		super();
		this.id = id;
	}

	public Game(int id, String name, GENRE genre, CONSOLES console, String publisher, String developer,
			LocalDate yearReleased) {
		super();
		this.id = id;
		this.name = name;
		this.genre = genre;
		this.console = console;
		this.publisher = publisher;
		this.developer = developer;
		this.dateReleased = yearReleased;
		this.multiplayer = false;
		this.available = true;
		this.avgRating = 0;
	}

	public Game(int id, String name, GENRE genre, CONSOLES console, String publisher, String developer,
			LocalDate yearReleased, boolean multiplayer) {
		super();
		this.id = id;
		this.name = name;
		this.genre = genre;
		this.console = console;
		this.publisher = publisher;
		this.developer = developer;
		this.dateReleased = yearReleased;
		this.multiplayer = multiplayer;
		this.available = true;
		this.avgRating = 0;
	}

	public Game(int id, String name, GENRE genre, CONSOLES console, String publisher, String developer,
			LocalDate yearReleased, boolean multiplayer, boolean available) {
		super();
		this.id = id;
		this.name = name;
		this.genre = genre;
		this.console = console;
		this.publisher = publisher;
		this.developer = developer;
		this.dateReleased = yearReleased;
		this.multiplayer = multiplayer;
		this.available = available;
		this.avgRating = 0;
	}
}
