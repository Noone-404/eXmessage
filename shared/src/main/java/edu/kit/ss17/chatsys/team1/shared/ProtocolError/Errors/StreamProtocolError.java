package edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.ErrorElement;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StreamErrorElement;

/**
 * Used to communicate stream errors.
 */
public class StreamProtocolError extends AbstractProtocolError {

	private final StreamErrorCondition condition;

	public StreamProtocolError(StreamErrorCondition condition, String msg) {
		super(msg, true);
		this.condition = condition;
	}

	public StreamErrorCondition getCondition() {
		return this.condition;
	}

	@Override
	public ErrorElement makeErrorElement() {
		return new StreamErrorElement(this.getCondition(), this.getErrorMessage());
	}
}
