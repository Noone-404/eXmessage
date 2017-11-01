package edu.kit.ss17.chatsys.team1.server.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.AbstractMessageSerializer;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.MessageSerializerInterface;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Message serializer for the server side. Use this class if you don't need to be able to serialize content fragments.
 */
public final class ServerMessageSerializer extends AbstractMessageSerializer {

	private static ServerMessageSerializer instance;

	/**
	 * @return this Singleton's instance.
	 */
	public static MessageSerializerInterface getInstance() {
		return (instance != null) ? instance : (instance = new ServerMessageSerializer());
	}

	@Override
	protected Document addExtendedContentXML(Element body, MessageInterface message) {
		body.addEntity("", message.getExtendedContentXML());
		return body.getDocument();
	}
}
