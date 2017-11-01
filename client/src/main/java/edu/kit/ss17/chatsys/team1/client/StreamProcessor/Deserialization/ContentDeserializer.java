package edu.kit.ss17.chatsys.team1.client.StreamProcessor.Deserialization;


import edu.kit.ss17.chatsys.team1.client.Model.RenderablePlaintextMessageStanza;
import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization.MessageDeserializer;


/**
 * Client side MessageDeserializer.
 */
public class ContentDeserializer extends MessageDeserializer {

	@Override
	protected MessageInterface createMessageInterface() {
		return new RenderablePlaintextMessageStanza(); // GUI can't use message objects
	}
}
