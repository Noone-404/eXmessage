package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorType;
import org.dom4j.Document;

/**
 * This is the exception to be thrown when an incoming Stanza-{@link Document} is malformed.
 */
public class StanzaErrorException extends StreamProcessorException {

	private final String message;
	private final String sender;
	private final String receiver;
	private final String stanzaType;
	private final String stanzaID;
	private final StanzaErrorType errorType;
	private final StanzaErrorCondition errorCondition;

	/**
	 * Constructor. Any of the parameters can be empty.
	 * @param sender the sender of the malformed stanza
	 * @param receiver the receiver of the malformed stanza.
	 * @param stanzaType the stanza type of the malformed stanza.
	 * @param stanzaID the stanzaID of the malformed stanza.
	 * @param message the error message.
	 */
	public StanzaErrorException(StanzaErrorCondition errorCondition, StanzaErrorType errorType, String stanzaType, String sender,
	                            String receiver, String stanzaID, String message) {
		this.errorCondition = errorCondition;
		this.errorType = errorType;
		this.stanzaType = stanzaType;
		this.sender = sender;
		this.receiver = receiver;
		this.stanzaID = stanzaID;
		this.message = message;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	/**
	 * @return the sender of the malformed stanza.
	 */
	public String getSender() {
		return this.sender;
	}

	/**
	 * @return the receiver of the malformed stanza.
	 */
	public String getReceiver() {
		return this.receiver;
	}

	/**
	 * @return the stanza type of the malformed stanza.
	 */
	public String getStanzaType() {
		return this.stanzaType;
	}

	/**
	 * @return the stanzaID of the malformed stanza.
	 */
	public String getStanzaID() {
		return this.stanzaID;
	}

	/**
	 * @return the error type, e.g. "continue".
	 */
	public StanzaErrorType getErrorType() {
		return errorType;
	}

	/**
	 * @return the error condition, e.g. "bad request".
	 */
	public StanzaErrorCondition getErrorCondition() {
		return errorCondition;
	}
}
