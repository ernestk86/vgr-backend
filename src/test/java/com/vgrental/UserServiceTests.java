package com.vgrental;

import static org.junit.Assert.assertEquals;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import com.vgrental.models.User;
import com.vgrental.exceptions.PasswordMismatchException;
import com.vgrental.models.ROLE;
import com.vgrental.models.STATES;
import com.vgrental.repositories.UserDAO;
import com.vgrental.services.UserService;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

	@InjectMocks
	UserService userService = new UserService(new SimpleMeterRegistry());

	@Mock
	UserDAO userDAO;

	@Mock
	User u;

	@Mock
	HttpServletRequest request;

	@Mock
	HttpSession session;

	@Mock
	Logger log;

	private static Optional<User> testUser;
	private static int testId = 1;

	@BeforeAll
	public static void setUpAll() {
		testUser = Optional.ofNullable(new User(1, "First", "password", LocalDate.of(1983, 1, 13), "123 Some St.",
				"City", STATES.MA, 12345, Collections.emptySet(), ROLE.CUSTOMER));
	}

	@AfterAll
	public static void tearDownAll() {
		testUser = null;
	}

	@Test
	void testFindAll() {
		System.out.println("Testing findAll()");

		List<User> result = new ArrayList<User>();
		result.add(testUser.get());
		result.add(new User(2, "Second", "password", LocalDate.of(1991, 6, 23), "234 Some St.", "Town", STATES.PA,
				23456, Collections.emptySet(), ROLE.CUSTOMER));
		result.add(new User(3, "Third", "password", LocalDate.of(2001, 5, 17), "345 Some St.", "Suburb", STATES.CA,
				34567, Collections.emptySet(), ROLE.CUSTOMER));
		result.add(new User(4, "Fourth", "password", LocalDate.of(2017, 11, 4), "456 Some St.", "Rural", STATES.LA,
				45678, Collections.emptySet(), ROLE.CUSTOMER));

		when(userDAO.findAll()).thenReturn(result);

		assertEquals(4, userService.findAll().size());

		verify(userDAO, times(1)).findAll();
	}

	@Test
	void testInsert() {
		System.out.println("Testing insert()");

		when(userDAO.save(u)).thenReturn(u);

		assertSame(u, userService.insert(u));
		verify(userDAO, times(1)).save(u);
	}

	@Test
	void testGetRentals() {
		System.out.println("Testing getRentals()");

		when(userDAO.findByUsername("First")).thenReturn(testUser);

		assertSame(testUser.get(), userService.findByUsername("First"));
		assertEquals(0, userService.getRentals("First").size());

		verify(userDAO, times(2)).findByUsername("First");
	}

	@Test
	void testFailedGetRentals() {
		System.out.println("Testing failed getRentals()");

		assertSame(Collections.emptySet(), userService.getRentals(u.getUsername()));

		verify(userDAO, times(1)).findByUsername(u.getUsername());
	}

	@Test
	void testFindByUsername() {
		System.out.println("Testing findByUsername()");

		when(userDAO.findByUsername("First")).thenReturn(testUser);
		assertSame(testUser.get(), userService.findByUsername("First"));
		verify(userDAO, times(1)).findByUsername("First");
	}

	@Test
	void testFailedFindByUsername() {
		System.out.println("Testing failed findByUsername()");

		assertNull(userService.findByUsername(u.getUsername()));
		verify(userDAO, times(1)).findByUsername(u.getUsername());
	}

	@Test
	void testFindById() {
		System.out.println("Testing findById()");
		Optional<User> testUser = Optional.ofNullable(new User(1, "First", "password", LocalDate.of(1983, 1, 13),
				"123 Some St.", "City", STATES.MA, 12345, Collections.emptySet(), ROLE.CUSTOMER));

		when(userDAO.findById(testId)).thenReturn(testUser);

		assertSame(testUser.get(), userService.findById(testId));
		verify(userDAO, times(testId)).findById(1);
	}

	@Test
	void testDeleteById() {
		System.out.println("Testing deleteById()");

		when(userDAO.findById(testId)).thenReturn(testUser);
		doNothing().when(userDAO).deleteById(testId);

		assertSame(testUser.get(), userService.findById(testId));
		assertTrue(userService.deleteById(testId));

		verify(userDAO, times(2)).findById(testId);
		verify(userDAO, times(1)).deleteById(testId);
	}

	@Test
	void testFailedDeleteById1() {
		assertFalse(userService.deleteById(u.getId()));

		verify(userDAO, times(1)).findById(u.getId());
	}

	@Test
	void testFailedDeleteById2() {
		when(userDAO.findById(testId)).thenReturn(testUser);
		System.out.println(testId);
		doThrow(IllegalArgumentException.class).when(userDAO).deleteById(testId);
		try {
			assertFalse(userService.deleteById(testId));
		} catch (IllegalArgumentException ex) {
			//
		}
		verify(userDAO, times(1)).findById(testId);
	}

	@Test
	void testLogin() {
		System.out.println("Testing login()");

		when(userDAO.findByUsername("First")).thenReturn(testUser);
		when(request.getSession()).thenReturn(session);

		assertSame(testUser.get(), userService.login("First", "password"));

		verify(userDAO, times(1)).findByUsername("First");
		verify(request, times(1)).getSession();
	}

	@Test
	void testFailedLogin() {
		System.out.println("Testing failed login()");

		when(userDAO.findByUsername("First")).thenReturn(testUser);

		Assertions.assertThrows(PasswordMismatchException.class, () -> {
			userService.login("First", "fail");
		});

		verify(userDAO, times(1)).findByUsername("First");
	}

	@Test
	void testLogout() {
		System.out.println("Testing logout()");

		when(request.getSession(false)).thenReturn(session);

		userService.logout();

		verify(session, times(1)).invalidate();
	}
}
