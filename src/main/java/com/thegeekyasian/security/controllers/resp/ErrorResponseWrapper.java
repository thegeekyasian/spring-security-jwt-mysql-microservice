package com.thegeekyasian.security.controllers.resp;

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
public class ErrorResponseWrapper {

	@Builder.Default
	private Boolean status = false;

	private String result;

	private Object message;

	private Object data;
}