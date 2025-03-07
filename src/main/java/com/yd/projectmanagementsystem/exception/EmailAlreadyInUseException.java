package com.yd.projectmanagementsystem.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6213065283581757856L;

	public EmailAlreadyInUseException(String message) {
        super(message);
    }
}