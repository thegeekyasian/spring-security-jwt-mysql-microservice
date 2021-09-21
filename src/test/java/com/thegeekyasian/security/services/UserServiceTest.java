package com.thegeekyasian.security.services;

import java.util.Optional;

import com.thegeekyasian.security.BaseServiceTest;
import com.thegeekyasian.security.exceptions.UnauthorizedException;
import com.thegeekyasian.security.models.AuthResponse;
import com.thegeekyasian.security.models.ChangePasswordRequest;
import com.thegeekyasian.security.models.RefreshTokenRequest;
import com.thegeekyasian.security.models.RegistrationRequest;
import com.thegeekyasian.security.persistence.entities.User;
import com.thegeekyasian.security.persistence.repositories.UserRepository;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.thegeekyasian.security.controllers.resp.CommonErrorHandler.DEFAULT_UNAUTHORIZED_ERROR;
import static com.thegeekyasian.security.controllers.resp.CommonErrorHandler.USER_NOT_FOUND_ERROR;
import static com.thegeekyasian.security.utils.AuthenticationUtils.generateAuthToken;
import static com.thegeekyasian.security.utils.AuthenticationUtils.generateRefreshToken;
import static com.thegeekyasian.security.utils.AuthenticationUtils.getSubjectFromToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author thegeekyasian.com
 */
@Import({ UserService.class })
public class UserServiceTest extends BaseServiceTest {

	@Autowired
	private UserService userService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private AuthenticationService authenticationService;

	@Test
	public void shouldRegisterUser() {

		RegistrationRequest registrationRequest = RegistrationRequest.builder()
				.username("test-username")
				.password("test-password")
				.confirmPassword("test-password")
				.build();

		when(userRepository.save(any(User.class)))
				.thenReturn(User.builder().username("test-username").build());

		AuthResponse authResponse = userService.registerUser(registrationRequest);

		assertThat(authResponse).isNotNull();
		assertThat(authResponse.getType()).isEqualTo("Bearer");
		assertThat(getSubjectFromToken(authResponse.getAuthToken()))
				.isEqualTo("test-username");
		assertThat(getSubjectFromToken(authResponse.getRefreshToken()))
				.isEqualTo("test-username");
		verify(userRepository, times(1))
				.save(any(User.class));
	}

	@Test
	public void shouldChangePassword() {

		final String username = "test-username";
		final ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
				.password("test-password")
				.newPassword("new-test-password")
				.confirmNewPassword("new-test-password")
				.build();
		final User user = mock(User.class);

		UsernamePasswordAuthenticationToken authentication =
				mock(UsernamePasswordAuthenticationToken.class);

		when(user.getUsername()).thenReturn(username);
		when(authentication.getPrincipal())
				.thenReturn(User.builder().username(username).build());
		SecurityContext securityContext = new SecurityContextImpl();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);

		when(userRepository.findByUsername(username))
				.thenReturn(Optional.of(user));
		when(userRepository.save(user))
				.thenReturn(user);

		AuthResponse authResponse = userService.changePassword(changePasswordRequest);

		assertThat(authResponse).isNotNull();
		assertThat(authResponse.getType()).isEqualTo("Bearer");
		assertThat(getSubjectFromToken(authResponse.getAuthToken()))
				.isEqualTo(username);
		assertThat(getSubjectFromToken(authResponse.getRefreshToken()))
				.isEqualTo(username);
		verify(user, times(1)).setPassword(anyString());
		verify(userRepository, times(1)).findByUsername(username);
		verify(userRepository, times(1)).save(user);
	}

	@Test
	public void shouldThrowException_WhenChangePasswordAndSecurityContextIsNull() {

		final ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
				.password("test-password")
				.newPassword("new-test-password")
				.confirmNewPassword("new-test-password")
				.build();

		SecurityContext securityContext = new SecurityContextImpl();
		securityContext.setAuthentication(null);
		SecurityContextHolder.setContext(securityContext);

		UnauthorizedException unauthorizedException = catchThrowableOfType(() ->
						userService.changePassword(changePasswordRequest),
				UnauthorizedException.class);

		assertThat(unauthorizedException).isNotNull();
		assertThat(unauthorizedException.getMessage())
				.isEqualTo(DEFAULT_UNAUTHORIZED_ERROR);
		verify(userRepository, times(0)).save(any(User.class));
	}

	@Test
	public void shouldRefreshToken() {

		final User user = User.builder().username("test-username").build();
		final String refreshToken = generateRefreshToken(user);
		final RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
				.refreshToken(refreshToken).build();

		when(userRepository.getByUsername(user.getUsername()))
				.thenReturn(user);

		AuthResponse authResponse = userService.refreshToken(refreshTokenRequest);

		assertThat(authResponse).isNotNull();
		assertThat(authResponse.getType()).isEqualTo("Bearer");
		assertThat(getSubjectFromToken(authResponse.getAuthToken()))
				.isEqualTo(user.getUsername());
		assertThat(getSubjectFromToken(authResponse.getRefreshToken()))
				.isEqualTo(user.getUsername());
		verify(userRepository, times(1))
				.getByUsername(user.getUsername());
	}

	@Test
	public void shouldThrowException_WhenRefreshTokenWithAuthToken() {

		final User user = User.builder().username("test-username").build();
		final String refreshToken = generateAuthToken(user);
		final RefreshTokenRequest refreshTokenRequest = RefreshTokenRequest.builder()
				.refreshToken(refreshToken).build();

		when(userRepository.getByUsername(user.getUsername()))
				.thenReturn(user);

		UnauthorizedException unauthorizedException = catchThrowableOfType(()
						-> userService.refreshToken(refreshTokenRequest),
				UnauthorizedException.class);

		assertThat(unauthorizedException).isNotNull();
		assertThat(unauthorizedException.getMessage())
				.isEqualTo(DEFAULT_UNAUTHORIZED_ERROR);
		verify(userRepository, times(0))
				.getByUsername(user.getUsername());
	}

	@Test
	public void shouldGFindUserByUsername() {

		final User user = User.builder().username("test-username").build();

		when(userRepository.findByUsername(user.getUsername()))
				.thenReturn(Optional.of(user));

		User u = userService.getUser(user.getUsername());

		assertThat(u).isNotNull();
		assertThat(user.getUsername()).isEqualTo(user.getUsername());
		verify(userRepository, times(1))
				.findByUsername(user.getUsername());
	}

	@Test
	public void shouldThrowExceptionWhenFindUserByUsername() {

		final User user = User.builder().username("test-username").build();

		when(userRepository.findByUsername(user.getUsername()))
				.thenReturn(Optional.empty());

		UsernameNotFoundException usernameNotFoundException = catchThrowableOfType(() ->
						userService.getUser(user.getUsername()),
				UsernameNotFoundException.class);

		assertThat(usernameNotFoundException).isNotNull();
		assertThat(usernameNotFoundException.getMessage())
				.isEqualTo(USER_NOT_FOUND_ERROR);
		verify(userRepository, times(1))
				.findByUsername(user.getUsername());
	}
}
