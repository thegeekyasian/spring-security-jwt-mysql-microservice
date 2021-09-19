package com.thegeekyasian.security.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author thegeekyasian.com
 */
@Getter
@AllArgsConstructor
public class UnauthorizedException extends RuntimeException {
	private final String message;
}
