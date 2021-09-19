package com.thegeekyasian.security.validators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.thegeekyasian.security.validators.RegistrationReqValidator;

/**
 * @author thegeekyasian.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RegistrationReqValidator.class)
public @interface ValidateRegistrationRequest {

	String message() default "Invalid registration request.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
