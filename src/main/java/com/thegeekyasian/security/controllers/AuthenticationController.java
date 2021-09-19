package com.thegeekyasian.security.controllers;

import javax.validation.Valid;

import com.thegeekyasian.security.controllers.resp.ResponseWrapper;
import com.thegeekyasian.security.models.AuthenticationRequest;
import com.thegeekyasian.security.models.RefreshTokenRequest;
import com.thegeekyasian.security.models.RegistrationRequest;
import com.thegeekyasian.security.services.IAuthenticationService;
import com.thegeekyasian.security.services.IUserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author thegeekyasian.com
 */
@RestController
@RequestMapping(value = "auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final IUserService userService;

	private final IAuthenticationService authenticationService;

	/**
	 * Login user
	 * <p>
	 * Logs in the user for the provided username and password.
	 * </p>
	 *
	 * @param authenticationRequest The AuthenticationReq holds a username and a password parameters.
	 * @return The function returns ResponseEntity having the ResponseWrapper. The success response holds a JWT token.
	 */
	@PostMapping(value = "login")
	public ResponseEntity<?> loginUser(
			@RequestBody @Valid AuthenticationRequest authenticationRequest) {
		return ResponseEntity.ok(ResponseWrapper
				.from(authenticationService.authenticateUser(authenticationRequest)));
	}

	/**
	 * Register user
	 * <p>
	 * Registers a new user for the provided username and password.
	 * Registration succeeds only if no user with the provided username already exists.
	 * </p>
	 *
	 * @param registrationRequest The RegistrationReq holds a username, a password and a confirm-password parameters.
	 * @return The function returns ResponseEntity having the ResponseWrapper. The success response holds a JWT token.
	 */
	@PostMapping(value = "register")
	public ResponseEntity<?> registerUser(
			@RequestBody @Valid RegistrationRequest registrationRequest) {
		return ResponseEntity.ok(ResponseWrapper
				.from(userService.registerUser(registrationRequest)));
	}

	@PostMapping(value = "refresh")
	public ResponseEntity<?> refreshToken(
			@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
		return ResponseEntity.ok(ResponseWrapper
				.from(userService.refreshToken(refreshTokenRequest)));
	}
}
