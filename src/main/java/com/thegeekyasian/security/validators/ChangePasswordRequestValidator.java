package com.thegeekyasian.security.validators;

import java.util.concurrent.atomic.AtomicReference;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.thegeekyasian.security.models.ChangePasswordRequest;
import com.thegeekyasian.security.persistence.repositories.UserRepository;
import com.thegeekyasian.security.validators.annotations.ValidateChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.thegeekyasian.security.controllers.resp.CommonErrorHandler.UNMATCHED_PASSWORDS_ERROR;
import static org.springframework.util.StringUtils.hasText;

/**
 * RegistrationReqValidator
 * <p>
 * Validates the RegistrationRequest before registering a user.
 * The validation has two parts:
 * </p>
 * <p>
 * 1. Validate if the provided password and confirm-password, both are equal.
 * </p>
 * <p>
 * 2. Validate if the user with that username does exist.
 * </p>
 *
 * <p>
 * If any of the above conditions is false, the validation fails.
 * </p>
 *
 * @author thegeekyasian.com
 */
@Slf4j
@RequiredArgsConstructor
public class ChangePasswordRequestValidator
		implements ConstraintValidator<ValidateChangePasswordRequest,
		ChangePasswordRequest> {

	private final UserRepository userRepository;

	@Override
	public void initialize(ValidateChangePasswordRequest validateChangePasswordRequest) {
	}

	@Override
	public boolean isValid(ChangePasswordRequest changePasswordRequest,
			ConstraintValidatorContext constraintValidatorContext) {

		AtomicReference<Boolean> isValid = new AtomicReference<>(Boolean.TRUE);
		constraintValidatorContext.disableDefaultConstraintViolation();

		// If the new-password and confirm-password do not match, return error.
		if (hasText(changePasswordRequest.getNewPassword())
				&& !changePasswordRequest.getNewPassword()
				.equals(changePasswordRequest.getConfirmNewPassword())) {

			isValid.set(Boolean.FALSE);
			constraintValidatorContext.buildConstraintViolationWithTemplate(
					UNMATCHED_PASSWORDS_ERROR).addConstraintViolation();
		}

		return isValid.get();
	}
}
