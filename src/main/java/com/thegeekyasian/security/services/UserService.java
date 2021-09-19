package com.thegeekyasian.security.services;

import java.util.List;
import java.util.Optional;

import com.thegeekyasian.security.enums.TokenType;
import com.thegeekyasian.security.exceptions.UnauthorizedException;
import com.thegeekyasian.security.models.AuthResponse;
import com.thegeekyasian.security.models.ChangePasswordRequest;
import com.thegeekyasian.security.models.RefreshTokenRequest;
import com.thegeekyasian.security.models.RegistrationRequest;
import com.thegeekyasian.security.persistence.entities.User;
import com.thegeekyasian.security.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.thegeekyasian.security.controllers.resp.CommonErrorHandler.DEFAULT_UNAUTHORIZED_ERROR;
import static com.thegeekyasian.security.controllers.resp.CommonErrorHandler.USER_NOT_FOUND_ERROR;
import static com.thegeekyasian.security.enums.Role.ROLE_USER;
import static com.thegeekyasian.security.utils.AuthenticationUtils.encodePassword;
import static com.thegeekyasian.security.utils.AuthenticationUtils.generateAuthToken;
import static com.thegeekyasian.security.utils.AuthenticationUtils.generateRefreshToken;
import static com.thegeekyasian.security.utils.AuthenticationUtils.getCurrentUser;
import static com.thegeekyasian.security.utils.AuthenticationUtils.getSubjectFromToken;
import static com.thegeekyasian.security.utils.AuthenticationUtils.isTokenIssuedAfterPasswordModified;
import static com.thegeekyasian.security.utils.AuthenticationUtils.isTokenValid;

/**
 * @author thegeekyasian.com
 */
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

	private final UserRepository userRepository;

	private final IAuthenticationService authenticationService;

	@Override
	public AuthResponse registerUser(RegistrationRequest registrationRequest) {

		String encodedPassword = encodePassword(registrationRequest.getPassword());

		User user = userRepository.save(User
				.builder()
				.username(registrationRequest.getUsername())
				.password(encodedPassword)
				.role(ROLE_USER)
				.build());

		return AuthResponse.from(generateAuthToken(user),
				generateRefreshToken(user));
	}

	@Override
	public AuthResponse changePassword(ChangePasswordRequest changePasswordRequest) {

		String username = Optional.ofNullable(getCurrentUser())
				.orElseThrow(() -> new UnauthorizedException(DEFAULT_UNAUTHORIZED_ERROR))
				.getUsername();

		authenticationService.authenticateUser(username,
				changePasswordRequest.getPassword());

		String encodedPassword = encodePassword(changePasswordRequest.getNewPassword());

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_ERROR));
		user.setPassword(encodedPassword);
		user = userRepository.save(user);

		return AuthResponse.from(generateAuthToken(user),
				generateRefreshToken(user));
	}

	@Override
	public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

		if (isTokenValid(refreshTokenRequest.getRefreshToken(), TokenType.REFRESH)) {
			String username = getSubjectFromToken(refreshTokenRequest.getRefreshToken());
			User user = userRepository.getByUsername(username);

			if (isTokenIssuedAfterPasswordModified(refreshTokenRequest.getRefreshToken(),
					user)) {
				return AuthResponse.from(generateAuthToken(user),
						generateRefreshToken(user));
			}
		}

		throw new UnauthorizedException(DEFAULT_UNAUTHORIZED_ERROR);
	}

	@Override
	public User getUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_ERROR));
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
}
