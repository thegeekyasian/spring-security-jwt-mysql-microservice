package com.thegeekyasian.security.models;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class RefreshTokenRequest {

	@NotBlank(message = "refresh_token must provided.")
	@JsonProperty("refresh_token")
	private String refreshToken;
}
