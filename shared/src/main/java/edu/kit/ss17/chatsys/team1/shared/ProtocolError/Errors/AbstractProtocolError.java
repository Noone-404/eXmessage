package edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.ErrorElement;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorInterface;

/**
 * Default {@Link ProtocolErrorInterface} implementation.
 */
public abstract class AbstractProtocolError implements ProtocolErrorInterface {

	private final String  message;
	private final Boolean isFatal;

	AbstractProtocolError(String msg, Boolean isFatal) {
		this.message = msg;
		this.isFatal = isFatal;
	}

	@Override
	public String getErrorMessage() {
		return this.message;
	}

	@Override
	public boolean isFatal() {
		return this.isFatal;
	}

	@Override
	public ErrorElement makeErrorElement() {
		return null;
	}
}
