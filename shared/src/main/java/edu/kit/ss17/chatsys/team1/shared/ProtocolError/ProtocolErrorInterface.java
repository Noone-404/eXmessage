package edu.kit.ss17.chatsys.team1.shared.ProtocolError;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.ErrorElement;

/**
 * Used to describe errors.
 */
public interface ProtocolErrorInterface {

	/**
	 * @return the error message
	 */
	String getErrorMessage();

	/**
	 * @return if this error is recoverable or not
	 */
	boolean isFatal();

	/**
	 * @return a serializable {@Link ErrorElement} subclass that is used inside a {@Link StreamProcessor}.
	 */
	ErrorElement makeErrorElement();
}
