package com.vgrental.models;

import java.time.LocalDate;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Future;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "rentals", schema = "rentals_vg")
@Data @AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(exclude = {"user", "game"}) @ToString(exclude = {"user", "game"})
public class Rental{
	
	@Id
	private int id;

	@JsonBackReference("UserRental")
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@JsonBackReference("GameRental")
	@OneToOne
	@JoinColumn(name = "game_id", nullable = false, unique = true, updatable = false)
	@MapsId
	private Game game;
	
	@Basic
	@JsonFormat (pattern = "yyyy-MM-dd")
	@Future
	private LocalDate dueDate;
	
	@Column(columnDefinition = "boolean default false")
	private boolean overDue;
	
	public Rental(User user, Game game) {
		super();
		this.user = user;
		this.game = game;
		this.dueDate = LocalDate.now().plusDays(14);
		this.overDue = false;
	}
}
