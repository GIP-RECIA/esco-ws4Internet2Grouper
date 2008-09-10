/**
 * 
 */
package org.esco.ws4Internet2Grouper.exceptions;

/**
 * Base exception thrown by the web service.
 * @author GIP RECIA - A. Deman
 * 26 nov. 07
 *
 */
public class WS4GrouperException extends RuntimeException {

	/** Serial Version UID.*/
	private static final long serialVersionUID = 4894843667914823550L;

	/**
	 * Constructor for WS4GrouperException.
	 */
	public WS4GrouperException() {
		super();
	}

	/**
	 * Constructor for WS4GrouperException.
	 * @param message The message displayed by the exception.
	 */
	public WS4GrouperException(final String message) {
		super(message);
	}
	
	/**
	 * Constructor for WS4GrouperException.
	 * @param wrappedException An exception to wrap.
	 */
	public WS4GrouperException(final Exception wrappedException) {
		super(wrappedException.getCause());
	}
	
	/**
	 * Constructor for WS4GrouperException.
	 * @param message The message to use add to the source exception.
	 * @param wrappedException The Grouper exception to wrap.
	 */
	public WS4GrouperException(final String message, final Exception wrappedException) {
		super(message, wrappedException.getCause());
	}

}
