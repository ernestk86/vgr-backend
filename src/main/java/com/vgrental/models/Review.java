package com.vgrental.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "reviews", schema = "rentals_vg")
@Data @AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(exclude = {"game"}) @ToString(exclude = {"game"})
public class Review {

	@Id
	@Column(nullable = false, unique = true, updatable = false)
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private int id;
	
	@JsonBackReference("GameReview")
	@ManyToOne
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;
	
	@Column(nullable = false)
	@Min(1)
	@Max(10)
	private int rating;
	
	@Column(nullable = false)
	@Length (min = 1)
	@NotBlank
	private String writtenReview;
}
