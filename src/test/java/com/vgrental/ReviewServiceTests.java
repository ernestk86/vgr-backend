package com.vgrental;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
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
import com.vgrental.models.Review;
import com.vgrental.repositories.ReviewDAO;
import com.vgrental.services.ReviewService;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTests {
	@InjectMocks
	ReviewService reviewService = new ReviewService(new SimpleMeterRegistry());

	@Mock
	ReviewDAO reviewDAO;

	@Mock
	Game g;

	@Mock
	Review r;

	private static final int testId = 1;
	private static Optional<Review> testReview;
	private static Game testGame;

	@BeforeAll
	public static void setUpAll() {
		testGame = new Game(1, "First", GENRE.ACTION_ADVENTURE, CONSOLES.SNES, "Publisher", "Developer",
				LocalDate.of(1983, 1, 13));
		testReview = Optional.ofNullable(new Review(1, testGame, 10, "First"));
	}

	@AfterAll
	public static void tearDownAll() {
		testReview = null;
	}

	@Test
	void testFindAll() {
		List<Review> result = new ArrayList<Review>();
		result.add(new Review(1, g, 10, "First review"));
		result.add(new Review(2, g, 6, "Second review"));

		when(reviewDAO.findAll()).thenReturn(result);

		assertEquals(2, reviewService.findAll().size());

		verify(reviewDAO, times(1)).findAll();
	}

	@Test
	void testInsert() {
		when(reviewDAO.save(r)).thenReturn(r);

		assertSame(r, reviewService.insert(r));

		verify(reviewDAO, times(1)).save(r);
	}

	@Test
	void testDeleteById() {
		doNothing().when(reviewDAO).deleteById(testId);

		assertTrue(reviewService.deleteById(testId));

		verify(reviewDAO, times(1)).deleteById(testId);
	}

	@Test
	void testFailedDeleteById() {
		doThrow(IllegalArgumentException.class).when(reviewDAO).deleteById(testId);
		try {
			assertFalse(reviewService.deleteById(testId));
		} catch (IllegalArgumentException ex) {

		}
		verify(reviewDAO, times(1)).deleteById(testId);
	}

	@Test
	void testGetAverageRating() {
		double testResult = 7.7;

		when(reviewDAO.getAverageRating(testId)).thenReturn(testResult);

		assertEquals(testResult, reviewService.getAverageRating(testId));

		verify(reviewDAO, times(1)).getAverageRating(testId);
	}

	@Test
	void testFindById() {
		when(reviewDAO.findById(r.getId())).thenReturn(testReview);

		assertSame(testReview.get(), reviewService.findById(r.getId()));

		verify(reviewDAO, times(1)).findById(r.getId());
	}

	@Test
	void testFailFindById() {
		assertNull(reviewService.findById(r.getId()));

		verify(reviewDAO, times(1)).findById(r.getId());
	}
}
