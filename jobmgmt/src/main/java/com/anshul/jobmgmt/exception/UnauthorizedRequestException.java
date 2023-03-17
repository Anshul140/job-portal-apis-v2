package com.anshul.jobmgmt.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnauthorizedRequestException extends Exception{

	private static final long serialVersionUID = 1L;
	Integer fieldValue;
	
	public UnauthorizedRequestException(String message, Integer fieldValue) {
		super(
			String
			.format("No job id: %d found associated with this user. Please try to update jobs created by you!"
					, fieldValue)
		);
		this.fieldValue = fieldValue;
	}
	
	public UnauthorizedRequestException(String message) {
		super(
			String.format(message)
		);
	}
}
