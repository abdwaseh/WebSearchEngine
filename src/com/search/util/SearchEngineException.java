package com.search.util;

public class SearchEngineException extends Exception {

	public SearchEngineException() {

	}

	public SearchEngineException(String message) {
		super(message);
	}

	public SearchEngineException(String message, Throwable cause) {
		super(message, cause);
	}

	public SearchEngineException(Throwable cause) {
		super(cause);
	}
}
