package com.thegeekyasian.security.services;

import java.util.List;

import com.thegeekyasian.security.models.AuthResponse;
import com.thegeekyasian.security.models.ChangePasswordRequest;
import com.thegeekyasian.security.models.RefreshTokenRequest;
import com.thegeekyasian.security.models.RegistrationRequest;
import com.thegeekyasian.security.persistence.entities.User;

/**
 * @author thegeekyasian.com
 */
public interface IUserService {

	AuthResponse registerUser(RegistrationRequest registrationRequest);

	AuthResponse changePassword(ChangePasswordRequest changePasswordRequest);

	AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

	User getUser(String username);

	List<User> getAllUsers();
}
