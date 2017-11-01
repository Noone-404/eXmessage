package edu.kit.ss17.chatsys.team1.client.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.AbstractMessageSerializer;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.MessageSerializerInterface;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Message serializer for the client side. Use this class to be able to serialize content fragments.
 */
public class ClientMessageSerializer extends AbstractMessageSerializer {

	private static ClientMessageSerializer instance;

	/**
	 * @return this Singleton's instance.
	 */
	public static MessageSerializerInterface getInstance() {
		return (instance != null) ? instance : (instance = new ClientMessageSerializer());
	}

	@Override
	protected Document addExtendedContentXML(Element body, MessageInterface message) {
		if (message.getExtendedContent() != null)
			body.add(message.getExtendedContent().serialize().getRootElement());
		return body.getDocument(); // TODO
	}
}
