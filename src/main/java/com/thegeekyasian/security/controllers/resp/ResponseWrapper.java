package com.thegeekyasian.security.controllers.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ResponseWrapper {

	@Builder.Default
	@JsonProperty(value = "status")
	private boolean status = true;

	@JsonProperty(value = "data")
	private Object data;

	public static ResponseWrapper from(Object data) {
		return ResponseWrapper.builder()
				.status(true)
				.data(data)
				.build();
	}
}