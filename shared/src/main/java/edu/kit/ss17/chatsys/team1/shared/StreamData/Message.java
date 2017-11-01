package edu.kit.ss17.chatsys.team1.shared.StreamData;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.MessageSerializerInterface;
import org.dom4j.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Message objects represent the client / client chat interaction. It can contain plaintext and / or extended content.
 */
public class Message extends AbstractStanza implements MessageInterface {

	private static MessageSerializerInterface messageSerializer;
	private String               plaintext          = "";
	private DataElementInterface extendedContent    = null;
	private String               extendedContentXML = "";
	private final Collection<MessageObserverInterface> messageObserverInterfaceCollection = new ArrayList<>();

	/**
	 * @param messageSerializer the serializer that is responsible for translating this object to a {@link Document}.
	 */
	public static void setMessageSerializer(MessageSerializerInterface messageSerializer) {
		Message.messageSerializer = messageSerializer;
	}

	/**
	 * @return the serializer that is responsible for translating this object to a {@link Document}.
	 */
	public static MessageSerializerInterface getMessageSerializer() {
		return messageSerializer;
	}

	@Override
	public Document serialize() {
		return messageSerializer.serialize(this);
	}

	@Override
	public String getPlaintextRepresentation() {
		return this.plaintext;
	}

	@Override
	public void setPlaintextRepresentation(String plain) {
		this.plaintext = plain;
		callObservers();
	}

	@Override
	public DataElementInterface getExtendedContent() {
		return this.extendedContent;
	}

	@Override
	public void setExtendedContent(DataElementInterface extendedContent) {
		this.extendedContent = extendedContent;
		callObservers();
	}

	@Override
	public void registerObserver(MessageObserverInterface observer) {
		this.messageObserverInterfaceCollection.add(observer);
	}

	@Override
	public void unregisterObserver(MessageObserverInterface observer) {
		this.messageObserverInterfaceCollection.remove(observer);
	}

	/**
	 * This method calls all observers of this message object. Should be called after this message object gets changed.
	 */
	private void callObservers() {
		for (final MessageObserverInterface observer : this.messageObserverInterfaceCollection) {
			observer.messageChanged(this);
		}
	}

	@Override
	public String getExtendedContentXML() {
		return this.extendedContentXML;
	}

	@Override
	public void setExtendedContentXML(String extendedContentXML) {
		this.extendedContentXML = extendedContentXML;
	}

	@Override
	public String toString() {
		return "Message{" +
		       "plaintext='" + this.plaintext + '\'' +
		       ", extendedContent=" + this.extendedContent +
		       ", extendedContentXML='" + this.extendedContentXML + '\'' +
		       "} " + super.toString();
	}
}
