package com.thegeekyasian.security.models;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thegeekyasian.security.validators.annotations.ValidateChangePasswordRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author thegeekyasian.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidateChangePasswordRequest
public class ChangePasswordRequest {

	@NotBlank(message = "password must be provided.")
	private String password;

	@NotBlank(message = "new_password must provided.")
	@JsonProperty("new_password")
	private String newPassword;

	@NotBlank(message = "confirm_new_password must provided.")
	@JsonProperty("confirm_new_password")
	private String confirmNewPassword;
}
