package edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorType;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.StanzaErrorSerializerInterface;
import org.dom4j.Document;

/**
 * Used by the method makeErrorElement() of {@Link StanzaProtocolError}.
 */
public class StanzaErrorElement extends ErrorElement {

	private static StanzaErrorSerializerInterface serializerInterface;

	private final String sender;
	private final String receiver;
	private final String stanzaType;
	private final String               stanzaID;
	private final StanzaErrorType      errorType;
	private final StanzaErrorCondition condition;

	public StanzaErrorElement(String sender, String receiver, String stanzaType, String stanzaID, StanzaErrorType error,
	                          StanzaErrorCondition condition, String message, boolean isFatal) {
		super(message, isFatal);
		this.sender = sender;
		this.receiver = receiver;
		this.stanzaType = stanzaType;
		this.stanzaID = stanzaID;
		this.errorType = error;
		this.condition = condition;
	}

	public static void setSerializerInterface(StanzaErrorSerializerInterface serializer) {
		serializerInterface = serializer;
	}

	public static StanzaErrorSerializerInterface getSerializerInterface() {
		return serializerInterface;
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

	public String getSender() {
		return sender;
	}

	public String getReceiver() {
		return receiver;
	}

	@Override
	public Document serialize() {
		if (serializerInterface == null)
			throw new IllegalStateException("No valid serializer interface set!");
		return serializerInterface.serialize(this);
	}

	@Override
	public String toString() {
		return "StanzaErrorElement{" +
		       "sender='" + sender + '\'' +
		       ", receiver='" + receiver + '\'' +
		       ", stanzaType='" + stanzaType + '\'' +
		       ", stanzaID='" + stanzaID + '\'' +
		       ", errorType=" + errorType +
		       ", condition=" + condition +
		       "} " + super.toString();
	}
}
