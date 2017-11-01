package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Constants;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * This abstract class provides the functionality to serialize Message objects.
 * Extending classes have to implement the serialization of extended content.
 */
public abstract class AbstractMessageSerializer extends AbstractStanzaSerializer implements MessageSerializerInterface {

	private static final String BODY = "body";

	/**
	 * Protected constructor to make Serializers Singletons.
	 */
	protected AbstractMessageSerializer() { // Not private to make accessible by subclasses.
	}

	@Override
	public Document serialize(DataElementInterface element) {
		if (!(element instanceof MessageInterface)) {
			throw new IllegalArgumentException();
		}
		MessageInterface message  = (MessageInterface) element;
		Document         document = DocumentHelper.createDocument();
		Element          root     = document.addElement(Constants.MESSAGE);
		root = serializeMetadata(root, message);
		Element body = root.addElement(BODY).addText(message.getPlaintextRepresentation());
		return addExtendedContentXML(body, message);
	}

	/**
	 * This method adds the serialization of the extended content of a message object to its Serialization-Document
	 *
	 * @param body    The (incomplete) serialization of the body of the message.
	 * @param message The message whose extended content should be serialized.
	 *
	 * @return The complete serialization of the message, including extended content.
	 */
	protected abstract Document addExtendedContentXML(Element body, MessageInterface message);
}
