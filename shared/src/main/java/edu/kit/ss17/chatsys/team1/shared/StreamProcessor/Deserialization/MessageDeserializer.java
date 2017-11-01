package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorType;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.Message;
import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Constants;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StreamErrorException;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StreamProcessorException;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class deserializes message objects.
 */
public class MessageDeserializer extends AbstractStanzaDeserializer {

	private static final String BODY    = "body";
	private final Collection<DeserializerInterface> contentFragmentDeserializerCollection = new ArrayList<>();

	/**
	 * This method uses a variation of the Chain of Responsibility pattern. The Document object gets passed to all content fragment deserializers until one of them recognizes the
	 * object and deserializes it.
	 *
	 * @param branch Document that gets deserialized to a DataElementInterface object.
	 */
	@Nullable
	@Override
	public DataElementInterface deserializeXML(Branch branch) throws StanzaErrorException {
		MessageInterface message = createMessageInterface();
		Element          root    = ((Document) branch).getRootElement();
		if (root == null) {
			throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
			                               Constants.EMPTY_XML_MESSAGE);
		}
		if (root.getName().equals(Constants.MESSAGE)) {
			message = (MessageInterface) deserializeMetadata(root, message);
			if (message != null) { // if message == null then deserializeMetadata decided it's not a stanza
				if (root.content().size() == 1 && ((Node) (root.content().get(0))).getName().equals(BODY)) {
					Element body = (Element) root.content().get(0);
					message.setPlaintextRepresentation(body.getText());
					if (body.elements().size() == 1) {
						message = deserializerExtendedContent((Element) body.elements().get(0), message);
					}
				} else {
					throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.MESSAGE, message.getSender(),
					                               message.getReceiver(), message.getID(), "A message stanza should have exactly one child of name body.");
				}
			}
		} else {
			message = null;
		}

		return message;
	}

	private MessageInterface deserializerExtendedContent(Element extendedContent, MessageInterface message) throws StanzaErrorException {
		try {
			for (final DeserializerInterface deserializer : this.contentFragmentDeserializerCollection) {
				DataElementInterface dataElement = deserializer.deserializeXML(extendedContent);
				if (dataElement != null) {
					message.setExtendedContent(dataElement);
					break;
				}
			}
		} catch (StanzaErrorException e) {
			throw e;
		} catch (StreamProcessorException ignore) { // this should not happen unless a plugin is broken in which case we don't care
		}

		message.setExtendedContentXML(extendedContent.asXML());
		return message;
	}

	/**
	 * @param deserializer DeserializerInterface to be available for extended message content deserialization.
	 */
	public void addDeserializer(DeserializerInterface deserializer) {
		this.contentFragmentDeserializerCollection.add(deserializer);
	}

	/**
	 * @return a new {@link MessageInterface}
	 */
	protected MessageInterface createMessageInterface() {
		return new Message();
	}
}
