package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StanzaErrorElement;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Translates a StanzaErrorElement into a {@link Document}.
 */
public final class StanzaErrorSerializer implements StanzaErrorSerializerInterface {

	private static StanzaErrorSerializerInterface instance;
	private static final String ID = "id", FROM = "from", TO = "to", TYPE = "type", ERROR = "error", TEXT = "text";

	/**
	 * Private constructor to make Serializers Singletons.
	 */
	private StanzaErrorSerializer() {
	}

	/**
	 * @return this Singleton's instance.
	 */
	public static StanzaErrorSerializerInterface getInstance() {
		return (instance != null) ? instance : (instance = new StanzaErrorSerializer());
	}

	@Override
	public Document serialize(DataElementInterface element) {
		if (!(element instanceof StanzaErrorElement)) {
			throw new IllegalArgumentException();
		}
		StanzaErrorElement stanzaErrorElement = (StanzaErrorElement) element;
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement(stanzaErrorElement.getStanzaType());
		root.addAttribute(FROM, stanzaErrorElement.getSender());
		root.addAttribute(ID, stanzaErrorElement.getStanzaID());
		root.addAttribute(TO, stanzaErrorElement.getReceiver());
		root.addAttribute(TYPE, ERROR);
		Element child = root.addElement(ERROR);
		child.addAttribute(TYPE, stanzaErrorElement.getErrorType().getName());
		child.addElement(stanzaErrorElement.getCondition().getName());
		child.addElement(TEXT).addText(stanzaErrorElement.getMessage());

		return document;
	}
}
