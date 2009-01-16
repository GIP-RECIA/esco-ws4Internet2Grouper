/**
 * 
 */
package org.esco.dynamicgroups.exceptions;

/**
 * Base exception for the dynamic groups management.
 * @author GIP RECIA - A. Deman
 * 26 nov. 07
 *
 */
public class DynamicGroupsException extends RuntimeException {

	/** Serial Version UID.*/
	private static final long serialVersionUID = 4894843667914823550L;

	/**
	 * Constructor for DynamicGroupsException.
	 */
	public DynamicGroupsException() {
		super();
	}

	/**
	 * Constructor for DynamicGroupsException.
	 * @param message The message displayed by the exception.
	 */
	public DynamicGroupsException(final String message) {
		super(message);
	}
	
	/**
	 * Constructor for DynamicGroupsException.
	 * @param wrappedException An exception to wrap.
	 */
	public DynamicGroupsException(final Exception wrappedException) {
		super(wrappedException.getCause());
	}
	
	/**
	 * Constructor for DynamicGroupsException.
	 * @param message The message to use add to the source exception.
	 * @param wrappedException The Grouper exception to wrap.
	 */
	public DynamicGroupsException(final String message, final Exception wrappedException) {
		super(message, wrappedException.getCause());
	}

}
