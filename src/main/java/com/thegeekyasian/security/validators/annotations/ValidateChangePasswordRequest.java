package com.thegeekyasian.security.validators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.thegeekyasian.security.validators.ChangePasswordRequestValidator;

/**
 * @author thegeekyasian.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ChangePasswordRequestValidator.class)
public @interface ValidateChangePasswordRequest {

	String message() default "Invalid change password request.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
