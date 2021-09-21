package com.thegeekyasian.security.services;

import javax.jws.soap.SOAPBinding.Use;

import com.thegeekyasian.security.BaseServiceTest;
import com.thegeekyasian.security.models.AuthResponse;
import com.thegeekyasian.security.models.AuthenticationRequest;
import com.thegeekyasian.security.persistence.entities.User;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static com.thegeekyasian.security.utils.AuthenticationUtils.getSubjectFromToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author thegeekyasian.com
 */
@Import({ AuthenticationService.class })
public class AuthenticationServiceTest extends BaseServiceTest {

	@Autowired
	private AuthenticationService authenticationService;

	@MockBean
	private AuthenticationManager authenticationManager;

	@Test
	public void shouldAuthenticateUserWithUsernameAndPassword() {

		when(authenticationManager.authenticate(
				any(UsernamePasswordAuthenticationToken.class)))
		.thenReturn(mock(UsernamePasswordAuthenticationToken.class));

		Object authenticationObject = authenticationService
				.authenticateUser("test-user", "test-password");

		assertThat(authenticationObject)
				.isInstanceOf(UsernamePasswordAuthenticationToken.class);
		verify(authenticationManager, times(1))
				.authenticate(any(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	public void shouldAuthenticateUserWithAuthenticationRequest() {

		AuthenticationRequest authenticationRequest = AuthenticationRequest
				.from("test-username", "test-password");
		UsernamePasswordAuthenticationToken authentication =
				mock(UsernamePasswordAuthenticationToken.class);

		when(authentication.getPrincipal())
				.thenReturn(User.builder().username("test-username").build());
		when(authenticationManager.authenticate(
				any(UsernamePasswordAuthenticationToken.class)))
		.thenReturn(authentication);

		AuthResponse authResponse = authenticationService
				.authenticateUser(authenticationRequest);

		assertThat(authResponse).isNotNull();
		assertThat(authResponse.getType()).isEqualTo("Bearer");
		assertThat(getSubjectFromToken(authResponse.getAuthToken()))
				.isEqualTo("test-username");
		assertThat(getSubjectFromToken(authResponse.getRefreshToken()))
				.isEqualTo("test-username");
		verify(authenticationManager, times(1))
				.authenticate(any(UsernamePasswordAuthenticationToken.class));
	}
}
