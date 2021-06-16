package com.vgrental;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vgrental.models.CONSOLES;
import com.vgrental.models.GENRE;
import com.vgrental.models.Game;
import com.vgrental.models.ROLE;
import com.vgrental.models.Rental;
import com.vgrental.models.STATES;
import com.vgrental.models.User;
import com.vgrental.repositories.GameDAO;
import com.vgrental.repositories.RentalDAO;
import com.vgrental.repositories.UserDAO;
import com.vgrental.services.RentalService;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@ExtendWith(MockitoExtension.class)
class RentalServiceTests {
	@InjectMocks
	RentalService rentalService = new RentalService(new SimpleMeterRegistry());

//	@Mock
//	RentalService mockRentalService;

	@Mock
	RentalDAO rentalDAO;

	@Mock
	UserDAO userDAO;

	@Mock
	GameDAO gameDAO;

	@Mock
	User u;

	@Mock
	Game g;

	@Mock
	Rental r;

	private static final int testId = 1;
	static Optional<Game> testGame;
	static Optional<User> testUser;
	static Optional<Rental> testRental;

	@BeforeAll
	public static void setUpAll() {
		testGame = Optional.ofNullable(new Game(1, "First", GENRE.ACTION_ADVENTURE, CONSOLES.SNES, "Publisher",
				"Developer", LocalDate.of(1983, 1, 13)));

		testUser = Optional.ofNullable(new User(1, "First", "password", LocalDate.of(1983, 1, 13), "123 Some St.",
				"City", STATES.MA, 12345, Collections.emptySet(), ROLE.CUSTOMER));

		testRental = Optional.ofNullable(new Rental(testUser.get(), testGame.get()));
	}

	@AfterAll
	public static void tearDownAll() {
		testGame = null;
		testUser = null;
		testRental = null;
	}

	@Test
	void testInsert() {
		System.out.println("Testing insert()");

		when(userDAO.findById(testUser.get().getId())).thenReturn(testUser);
		when(gameDAO.findById(testGame.get().getId())).thenReturn(testGame);
		when(rentalDAO.save(testRental.get())).thenReturn(testRental.get());
		when(gameDAO.save(testGame.get())).thenReturn(testGame.get());

		assertSame(testRental.get(), rentalService.insert(1, 1));

		verify(userDAO, times(1)).findById(testUser.get().getId());
		verify(gameDAO, times(1)).findById(testGame.get().getId());
		verify(rentalDAO, times(1)).save(testRental.get());
		verify(gameDAO, times(1)).save(testGame.get());
	}

	@Test
	void testFailedInsert() {
		System.out.println("Testing failed insert()");

		assertNull(rentalService.insert(testUser.get().getId(), testGame.get().getId()));

		verify(userDAO, times(1)).findById(testUser.get().getId());
		verify(gameDAO, times(1)).findById(testGame.get().getId());
	}

	@Test
	void testDeleteById() {
		System.out.println("Testing deleteById()");

		doNothing().when(rentalDAO).deleteById(testId);
		when(gameDAO.findById(testId)).thenReturn(testGame);
		when(gameDAO.save(testGame.get())).thenReturn(testGame.get());

		assertTrue(rentalService.deleteById(testId));

		verify(rentalDAO, times(1)).deleteById(testId);
		verify(gameDAO, times(1)).findById(testId);
		verify(gameDAO, times(1)).save(testGame.get());
	}

	@Test
	void testFailedDeleteById1() {
		System.out.println("Testing first failed deleteById()");

		doNothing().when(rentalDAO).deleteById(testId);

		assertFalse(rentalService.deleteById(testId));

		verify(rentalDAO, times(1)).deleteById(testId);
		verify(gameDAO, times(1)).findById(testId);
	}

	@Test
	void testFailedDeleteById2() {
		System.out.println("Testing second failed deleteById()");

		doThrow(IllegalArgumentException.class).when(rentalDAO).deleteById(testId);
		try {
			assertFalse(rentalService.deleteById(testId));
		} catch (IllegalArgumentException ex) {

		}
		verify(rentalDAO, times(1)).deleteById(testId);
	}

	@Test
	void testToggleOverdue() {
		System.out.println("Testing toggleOverdue()");

		when(rentalDAO.findById(testId)).thenReturn(testRental);

		assertSame(testRental.get().getGame(), rentalService.toggleOverdue(testId).getGame());
		assertSame(testRental.get().getUser(), rentalService.toggleOverdue(testId).getUser());
		assertEquals(testRental.get().getDueDate(), rentalService.toggleOverdue(testId).getDueDate());
		assertNotEquals(testRental.get().isOverDue(), rentalService.toggleOverdue(testId).isOverDue());

		verify(rentalDAO, times(4)).findById(testId);
		verify(rentalDAO, times(4)).save(testRental.get());
	}

	@Test
	public void testFailedToggleOverdue() {
		System.out.println("Testing toggleOverdue() when rental doesn't exist");

		assertNull(rentalService.toggleOverdue(g.getId()));

		verify(rentalDAO, times(1)).findById(g.getId());
	}

	@Test
	public void testChangeDate() {
		System.out.println("Testing changeDate()");

		when(rentalDAO.findById(testGame.get().getId())).thenReturn(testRental);

		assertEquals(LocalDate.of(1991, 6, 23),
				rentalService.changeDate(testGame.get().getId(), LocalDate.of(1991, 6, 23)).getDueDate());

		verify(rentalDAO, times(1)).findById(testGame.get().getId());
		verify(rentalDAO, times(1)).save(testRental.get());
	}

	@Test
	void testFailedSendEmail1() {
		System.out.println("Testing first failed sendEmail()");
		rentalService.sendEmail(testRental.get());
		verify(userDAO, times(1)).findById(testRental.get().getUser().getId());
	}

	@Test
	void testFailedSendEmail2() {
		System.out.println("Testing second failed sendEmail()");

		when(userDAO.findById(testRental.get().getUser().getId())).thenReturn(testUser);

		rentalService.sendEmail(testRental.get());
		verify(userDAO, times(1)).findById(testRental.get().getUser().getId());
		verify(gameDAO, times(1)).findById(testRental.get().getGame().getId());
	}
}
