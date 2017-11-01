package edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.dom4j.Document;

/**
 * Serializable error object that is used inside a {@Link StreamProcessor}.
 */
public abstract class ErrorElement implements DataElementInterface {

	private final String message;
	private final boolean isFatal;

	protected ErrorElement(String message, boolean isFatal) {
		this.message = message;
		this.isFatal = isFatal;
	}

	@Override
	public Document serialize() {
		return null;
	}

	public String getMessage() {
		return message;
	}

	public boolean isFatal() {
		return isFatal;
	}

	@Override
	public String toString() {
		return "ErrorElement{" +
		       "message='" + message + '\'' +
		       ", isFatal=" + isFatal +
		       '}';
	}
}
