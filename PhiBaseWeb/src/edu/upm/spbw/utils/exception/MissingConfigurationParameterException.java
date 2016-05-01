package edu.upm.spbw.utils.exception;

public class MissingConfigurationParameterException extends Exception {

	/**
	 * ID Serializacion
	 */
	private static final long serialVersionUID = 1L;

	public MissingConfigurationParameterException() {
	}

	public MissingConfigurationParameterException(String message) {
		super(message);
	}

	public MissingConfigurationParameterException(String message,
			Throwable cause) {
		super(message, cause);
	}

	public MissingConfigurationParameterException(Throwable cause) {
		super(cause);
	}
}
