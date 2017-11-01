package edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorType;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.ErrorElement;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StanzaErrorElement;

/**
 * Used to communicate stanza errors.
 */
public class StanzaProtocolError extends AbstractProtocolError {

	private final String sender;
	private final String receiver;
	private final String stanzaType;
	private final String               stanzaID;
	private final StanzaErrorType      errorType;
	private final StanzaErrorCondition condition;

	public StanzaProtocolError(String sender, String receiver, String stanzaType, String stanzaID, StanzaErrorType type,
	                           StanzaErrorCondition condition, String msg, Boolean isFatal) {
		super(msg, isFatal);
		this.sender = sender;
		this.receiver = receiver;
		this.stanzaType = stanzaType;
		this.stanzaID = stanzaID;
		this.errorType = type;
		this.condition = condition;
	}

	public String getStanzaType() {
		return this.stanzaType;
	}

	public String getStanzaID() {
		return this.stanzaID;
	}

	public StanzaErrorType getErrorType() {
		return this.errorType;
	}

	public StanzaErrorCondition getCondition() {
		return this.condition;
	}

	@Override
	public ErrorElement makeErrorElement() {
		return new StanzaErrorElement(this.getSender(), this.getReceiver(), this.getStanzaType(), this.getStanzaID(),
		                              this.getErrorType(), this.getCondition(), this.getErrorMessage(), this.isFatal());
	}

	public String getSender() {
		return sender;
	}

	public String getReceiver() {
		return receiver;
	}
}
