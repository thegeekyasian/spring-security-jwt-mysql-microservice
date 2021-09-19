package com.thegeekyasian.security.services;

import com.thegeekyasian.security.models.AuthResponse;
import com.thegeekyasian.security.models.AuthenticationRequest;

import org.springframework.stereotype.Service;

/**
 * @author thegeekyasian.com
 */
@Service
public interface IAuthenticationService {

	Object authenticateUser(String username, String password);

	AuthResponse authenticateUser(AuthenticationRequest authenticationRequest);
}
