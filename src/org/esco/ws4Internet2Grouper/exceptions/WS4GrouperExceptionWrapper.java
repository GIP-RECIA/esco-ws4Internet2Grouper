/**
 * 
 */
package org.esco.ws4Internet2Grouper.exceptions;

/**
 * Exception used by the web sevice used to wrap a Grouper exception.
 * @author GIP RECIA - A. Deman
 * 26 nov. 07
 */
public class WS4GrouperExceptionWrapper extends WS4GrouperException {
	
	/**Serial Version UID.*/
	private static final long serialVersionUID = -7288015012953680653L;

	/**
	 * Constructor for WS4GrouperException.
	 * @param wrappedException The Grouper exception to wrap.
	 */
	public WS4GrouperExceptionWrapper(final Exception wrappedException) {
		super(wrappedException);
	}

	/**
	 * Constructor for WS4GrouperException.
	 * @param message The message to use add to the source exception.
	 * @param wrappedException The Grouper exception to wrap.
	 */
	public WS4GrouperExceptionWrapper(final String message, final Exception wrappedException) {
		super(message, wrappedException);
	}
}
