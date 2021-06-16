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
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
/* Creates table if doesn't exist
*  Fields are only defined when table is generated.
*  Fields are NOT altered if table already exists
*  and you change definitions in source code here.
*/
@Table(name = "users", schema = "rentals_vg")
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(exclude = {"rentals"}) @ToString(exclude = {"rentals"})
public class User {
	@Id
	@Column(nullable = false, unique = true, updatable = false)
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(nullable = false, unique = true, updatable = false) // MUST SET COLUMN AS UNIQUE IN DB
	@Length (min = 1)
	@NotBlank
	@Pattern(regexp = "[a-zA-Z][a-zA-Z0-9]*")
	private String username;
	
	@Column(nullable = false)
	@Length (min = 1)
	@NotBlank
	private String password;
	
	@Column(nullable = false)
	@Basic
	@JsonFormat (pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;
	
	@Column(nullable = false)
	@Length (min = 1)
	@NotBlank
	private String address;
	
	@Column(nullable = false)
	@Length (min = 1)
	@NotBlank
	private String city;
	
	@Column(nullable = false)
	@Enumerated (EnumType.STRING)
	private STATES state;
	
	@Column(nullable = false)
	@Min(1000)
	@Max(99999)
	private int zipCode;
	
	@JsonManagedReference("UserRental")
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private Set<Rental> rentals;
	
	@Column(nullable = false)
	@Enumerated (EnumType.STRING)
	private ROLE role = ROLE.CUSTOMER;
	
	public User(int id) {
		super();
		this.id = id;
	}
}
