package com.thegeekyasian.security.controllers;

import javax.validation.Valid;

import com.thegeekyasian.security.controllers.resp.ResponseWrapper;
import com.thegeekyasian.security.models.ChangePasswordRequest;
import com.thegeekyasian.security.services.IUserService;
import com.thegeekyasian.security.utils.AuthenticationUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author thegeekyasian.com
 */
@RestController
@RequestMapping(value = "users")
@RequiredArgsConstructor
public class UserController {

	private final IUserService userService;

	/**
	 * Change password
	 * <p>
	 * Updates user's password to the latest one.
	 * Change-password succeeds only if the user is authenticated.
	 * </p>
	 *
	 * @param changePasswordRequest The ChangePasswordRequest holds password
	 * (current password), newPassword and a confirmNewPassword parameters.
	 * @return The function returns ResponseEntity having the ResponseWrapper.
	 * The success response holds a JWT token.
	 */
	@PostMapping(value = "change-password")
	public ResponseEntity<?> changePassword(
			@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
		return ResponseEntity.ok(ResponseWrapper
				.from(userService.changePassword(changePasswordRequest)));
	}

	@GetMapping(value = "me")
	public ResponseEntity<ResponseWrapper> getCurrentUser() {
		return ResponseEntity
				.ok()
				.body(ResponseWrapper.from(AuthenticationUtils.getCurrentUser()));
	}
}
