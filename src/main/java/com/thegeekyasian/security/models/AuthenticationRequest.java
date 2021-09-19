package com.thegeekyasian.security.models;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author thegeekyasian.com
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

	@NotBlank(message = "username must be provided.")
	private String username;

	@NotBlank(message = "password must be provided.")
	private String password;

	public static AuthenticationRequest from(String username, String password) {

		return AuthenticationRequest.builder()
				.username(username)
				.password(password)
				.build();
	}
}
