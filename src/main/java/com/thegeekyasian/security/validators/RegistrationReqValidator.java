package com.thegeekyasian.security.validators;

import java.util.concurrent.atomic.AtomicReference;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.thegeekyasian.security.models.RegistrationRequest;
import com.thegeekyasian.security.persistence.repositories.UserRepository;
import com.thegeekyasian.security.validators.annotations.ValidateRegistrationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.thegeekyasian.security.controllers.resp.CommonErrorHandler.UNMATCHED_PASSWORDS_ERROR;
import static com.thegeekyasian.security.controllers.resp.CommonErrorHandler.USER_ALREADY_EXISTS;
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
 * 2. Validate that a user with similar username doesn't already exist.
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
public class RegistrationReqValidator
		implements ConstraintValidator<ValidateRegistrationRequest, RegistrationRequest> {

	private final UserRepository userRepository;

	@Override
	public void initialize(ValidateRegistrationRequest validateRegistrationRequest) {
	}

	@Override
	public boolean isValid(RegistrationRequest registrationRequest,
			ConstraintValidatorContext constraintValidatorContext) {

		AtomicReference<Boolean> isValid = new AtomicReference<>(Boolean.TRUE);
		constraintValidatorContext.disableDefaultConstraintViolation();

		// If the password and confirm-password do not match, return error.
		if (hasText(registrationRequest.getConfirmPassword())
				&& !registrationRequest.getConfirmPassword()
				.equals(registrationRequest.getPassword())) {
			isValid.set(Boolean.FALSE);
			constraintValidatorContext
					.buildConstraintViolationWithTemplate(UNMATCHED_PASSWORDS_ERROR)
					.addConstraintViolation();
		}

		// If a user with similar username already exists, return error.
		userRepository.findByUsername(registrationRequest.getUsername())
				.ifPresent(user -> {
					isValid.set(Boolean.FALSE);
					constraintValidatorContext
							.buildConstraintViolationWithTemplate(USER_ALREADY_EXISTS)
							.addConstraintViolation();
				});

		return isValid.get();
	}
}
