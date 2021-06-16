package com.vgrental;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vgrental.exceptions.NotAuthorizedException;
import com.vgrental.exceptions.NotLoggedInException;
import com.vgrental.models.ROLE;
import com.vgrental.models.STATES;
import com.vgrental.models.User;
import com.vgrental.services.AuthorizationService;

@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceTests {
	@InjectMocks
	AuthorizationService authorizationService;
	
	@Mock
	HttpServletRequest request;
	
	@Mock
	HttpSession session;
	
	private static User currentUser;
	private static int notCurrentUserId;
	
	@BeforeAll
	public static void setUpAll() {
		currentUser = new User(1, "First", "password", LocalDate.of(1983, 1, 13), 
			"123 Some St.", "City", STATES.MA, 12345, Collections.emptySet(), ROLE.CUSTOMER);
		notCurrentUserId = 0;
	}
	
	@AfterAll
	public static void tearDownAll() {
		currentUser = null;
	}
	
	@Test
	public void testGuardByUserId() {
		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("currentUser")).thenReturn(currentUser);
		
		authorizationService.guardByUserId(currentUser.getId());
		
		verify(request, times(1)).getSession(false);
		verify(session, times(2)).getAttribute("currentUser");
	}
	
	@Test//(expected = NotLoggedInException.class)
	public void testNotLoggedInException() {
		when(request.getSession(false)).thenReturn(null);
		//authorizationService.guardByUserId(currentUser.getId());
		
		Assertions.assertThrows(NotLoggedInException.class, () -> {
			authorizationService.guardByUserId(currentUser.getId());
		});
		
		verify(request, times(1)).getSession(false);
	}

	@Test//(expected = NotAuthorizedException.class)
	public void testNotAuthorizedException() {
		when(request.getSession(false)).thenReturn(session);
		when(session.getAttribute("currentUser")).thenReturn(currentUser);
		
		//authorizationService.guardByUserId(notCurrentUserId);
		
		Assertions.assertThrows(NotAuthorizedException.class, () -> {
			authorizationService.guardByUserId(notCurrentUserId);
		});
		
		verify(request, times(1)).getSession(false);
		verify(session, times(2)).getAttribute("currentUser");
	}
}
