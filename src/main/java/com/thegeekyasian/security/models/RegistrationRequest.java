package com.thegeekyasian.security.models;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thegeekyasian.security.validators.annotations.ValidateRegistrationRequest;
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
@ValidateRegistrationRequest
public class RegistrationRequest extends AuthenticationRequest {

	@NotBlank(message = "confirm_password must provided.")
	@JsonProperty("confirm_password")
	private String confirmPassword;
}
