package com.thegeekyasian.security.controllers.resp;


import java.util.stream.Collectors;

import com.thegeekyasian.security.exceptions.UnauthorizedException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author thegeekyasian.com
 */
@RestControllerAdvice
@Slf4j
public class CommonErrorHandler {
	public static final String DEFAULT_BAD_REQUEST_ERROR = "Bad request to service";

	public static final String DEFAULT_UNAUTHORIZED_ERROR = "You're not authorized to access this resource.";

	public static final String DEFAULT_BAD_CREDENTIALS_ERROR = "Invalid username or password";

	public static final String USER_NOT_FOUND_ERROR = "No user found for the provided username.";

	public static final String UNMATCHED_PASSWORDS_ERROR = "Passwords do not match.";

	public static final String USER_ALREADY_EXISTS = "A user with this username already exists.";

	public static ErrorResponseWrapper generateError(int errorCode, Object message) {
		return ErrorResponseWrapper.builder()
				.result(String.valueOf(errorCode))
				.message(message)
				.build();
	}

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorResponseWrapper methodArgumentNotValidException(
			MethodArgumentNotValidException ex) {
		return generateError(BAD_REQUEST.value(),
				ex.getBindingResult()
						.getAllErrors()
						.stream()
						.map(DefaultMessageSourceResolvable::getDefaultMessage)
						.collect(Collectors.toList()));
	}

	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	ErrorResponseWrapper unauthorized(UnauthorizedException e) {
		return generateError(HttpStatus.UNAUTHORIZED.value(),
				StringUtils.hasText(e.getMessage()) ?
						DEFAULT_UNAUTHORIZED_ERROR :
						e.getMessage());
	}

	@ExceptionHandler(MalformedJwtException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	ErrorResponseWrapper malformedJwtException() {
		return generateError(HttpStatus.UNAUTHORIZED.value(),
				"Invalid authentication token.");
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ErrorResponseWrapper missingRequestParams() {
		return generateError(HttpStatus.BAD_REQUEST.value(),
				DEFAULT_BAD_REQUEST_ERROR);
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	ErrorResponseWrapper notFound(UsernameNotFoundException e) {
		return generateError(HttpStatus.NOT_FOUND.value(), e.getMessage());
	}

	@ExceptionHandler(BadCredentialsException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	ErrorResponseWrapper notFound(BadCredentialsException e) {
		return generateError(HttpStatus.UNAUTHORIZED.value(),
				DEFAULT_BAD_CREDENTIALS_ERROR);
	}
}