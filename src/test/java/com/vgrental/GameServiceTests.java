package com.vgrental;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import com.vgrental.repositories.GameDAO;
import com.vgrental.services.GameService;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@ExtendWith(MockitoExtension.class)
class GameServiceTests {
	@InjectMocks
	GameService gameService = new GameService(new SimpleMeterRegistry());

	@Mock
	GameDAO gameDAO;

	@Mock
	Game g;

	static Optional<Game> testGame;

	@BeforeAll
	public static void setUpAll() {
		testGame = Optional.ofNullable(new Game(1, "First", GENRE.ACTION_ADVENTURE, CONSOLES.SNES, "Publisher",
				"Developer", LocalDate.of(1983, 1, 13)));
	}

	@AfterAll
	public static void tearDownAll() {
		testGame = null;
	}

	@Test
	void testFindAll() {
		System.out.println("Testing findAll()");

		List<Game> result = new ArrayList<Game>();
		result.add(testGame.get());
		result.add(new Game(2, "Second", GENRE.BATTLE_ROYALE, CONSOLES.DREAMCAST, "2nd Publisher", "2nd Developer",
				LocalDate.of(1983, 1, 13)));
		result.add(new Game(3, "Third", GENRE.FIRST_PERSON_SHOOTER, CONSOLES.NES, "3rd Publisher", "3rd Developer",
				LocalDate.of(1983, 1, 13)));

		when(gameDAO.findAll()).thenReturn(result);

		assertEquals(3, gameService.findAll().size());

		verify(gameDAO, times(1)).findAll();
	}

	@Test
	void testInsert() {
		System.out.println("Testing insert()");

		when(gameDAO.save(g)).thenReturn(g);
		Game nullGame = null;

		assertSame(g, gameService.insert(g));
		assertSame(nullGame, gameService.insert(nullGame));

		verify(gameDAO, times(1)).save(g);
	}

	@Test
	void testFindById() {
		System.out.println("Testing findById()");

		when(gameDAO.findById(1)).thenReturn(testGame);

		assertSame(testGame.get(), gameService.findById(1));
		assertNull(gameService.findById(g.getId()));

		verify(gameDAO, times(1)).findById(1);
		verify(gameDAO, times(1)).findById(g.getId());
	}

	@Test
	void testDeleteById() {
		System.out.println("Testing deleteById()");

		doNothing().when(gameDAO).deleteById(1);

		gameService.deleteById(1);

		verify(gameDAO, times(1)).deleteById(1);
	}

	@Test
	void testFailedDeleteById() {
		System.out.println("Testing failed deleteById()");

		doThrow(IllegalArgumentException.class).when(gameDAO).deleteById(1);
		try {
			assertFalse(gameService.deleteById(1));
		} catch (IllegalArgumentException ex) {

		}
		verify(gameDAO, times(1)).deleteById(1);
	}

	@Test
	void testSearchByName() {
		System.out.println("Testing searchByName()");

		List<Game> result = new ArrayList<Game>();
		result.add(testGame.get());
		result.add(new Game(2, "Second", GENRE.BATTLE_ROYALE, CONSOLES.DREAMCAST, "2nd Publisher", "2nd Developer",
				LocalDate.of(1983, 1, 13)));
		result.add(new Game(3, "Sthird", GENRE.FIRST_PERSON_SHOOTER, CONSOLES.NES, "3rd Publisher", "3rd Developer",
				LocalDate.of(1983, 1, 13)));

		when(gameDAO.searchByName("S")).thenReturn(result);

		assertSame(result, gameService.searchByName("S"));

		verify(gameDAO, times(1)).searchByName("S");
	}
}
