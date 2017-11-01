package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.StanzaInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Constants;
import org.dom4j.Element;

import java.time.Instant;

/**
 * This abstract class provides the functionality to serialize the metadata of stanza objects.
 */
public abstract class AbstractStanzaSerializer implements StanzaSerializerInterface {

	/**
	 * This method translates the metadata of a stanza to its XML representation.
	 * @param element the {@link Element} to put the serialization in.
	 * @param stanza the {@link StanzaInterface} to be serialized.
	 * @return the given {@link Element} with the serialized values in it.
	 */
	static Element serializeMetadata(Element element, StanzaInterface stanza) {
		element.addAttribute("from", stanza.getSender())
		       .addAttribute("id", stanza.getID()).addAttribute("to", stanza.getReceiver())
		       .addAttribute(Constants.CLIENT_SEND_DATE, instantToString(stanza.getClientSendDate()))
		       .addAttribute(Constants.SERVER_RECEIVE_DATE, instantToString(stanza.getServerReceiveDate()))
		       .addAttribute(Constants.CLIENT_RECEIVE_DATE, instantToString(stanza.getClientReceiveDate()))
		       .addAttribute("type", stanza.getType());
		return element;
	}

	private static String instantToString(Instant instant) {
		String instantString = "";
		if (instant != null) {
			instantString = String.valueOf(instant.getEpochSecond()).concat(".").concat(String.valueOf(instant.getNano()));
		}
		return instantString;
	}
}
