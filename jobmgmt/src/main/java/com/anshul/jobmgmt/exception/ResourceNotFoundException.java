package com.anshul.jobmgmt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(Object resourceId) {
		super(resourceId != null ? resourceId.toString():null);
	}
	
	public ResourceNotFoundException(String message) {
		super(String.format(message));
	}
}
