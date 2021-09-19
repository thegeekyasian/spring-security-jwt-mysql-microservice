package com.thegeekyasian.security.services;

import com.thegeekyasian.security.models.AuthResponse;
import com.thegeekyasian.security.models.AuthenticationRequest;
import com.thegeekyasian.security.persistence.entities.User;
import com.thegeekyasian.security.utils.AuthenticationUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;


/**
 * @author thegeekyasian.com
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

	private final AuthenticationManager authenticationManager;

	@Override
	public Object authenticateUser(String username, String password) {
		return authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password));
	}

	/**
	 * Authenticate user
	 * <p>
	 *     The method uses the username and password from the request
	 * </p>
	 * */
	@Override
	public AuthResponse authenticateUser(AuthenticationRequest authenticationRequest) {

		User user = (User) authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						authenticationRequest.getUsername(),
						authenticationRequest.getPassword()))
				.getPrincipal();

		return AuthResponse.from(AuthenticationUtils.generateAuthToken(user),
				AuthenticationUtils.generateRefreshToken(user));
	}
}
