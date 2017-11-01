package edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.StreamErrorSerializerInterface;
import org.dom4j.Document;

/**
 * Used by the method makeErrorElement() of {@Link StreamProtocolError}.
 */
public class StreamErrorElement extends ErrorElement {

	private static StreamErrorSerializerInterface serializerInterface;

	private final StreamErrorCondition condition;

	public StreamErrorElement(StreamErrorCondition condition, String message) {
		super(message, true);
		this.condition = condition;
	}

	public static void setSerializerInterface(StreamErrorSerializerInterface serializer) {
		serializerInterface = serializer;
	}

	public static StreamErrorSerializerInterface getSerializerInterface() {
		return serializerInterface;
	}

	public StreamErrorCondition getCondition() {
		return this.condition;
	}

	@Override
	public Document serialize() {
		if (serializerInterface == null)
			throw new IllegalStateException("No valid serializer interface set!");
		return serializerInterface.serialize(this);
	}

	@Override
	public String toString() {
		return "StreamErrorElement{" +
		       "condition=" + condition +
		       "} " + super.toString();
	}
}
