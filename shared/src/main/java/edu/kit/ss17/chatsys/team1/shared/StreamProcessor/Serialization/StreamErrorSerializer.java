package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StreamErrorElement;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * This class translates a StreamErrorElement to a {@link Document}.
 */
public final class StreamErrorSerializer implements StreamErrorSerializerInterface {

	private static StreamErrorSerializer instance;
	private static final String ERROR = "stream-error", TEXT = "text";

	/**
	 * Private constructor to make serializers singletons.
	 */
	private StreamErrorSerializer() {
	}

	/**
	 * @return this Singleton's instance.
	 */
	public static StreamErrorSerializer getInstance() {
		return (instance != null) ? instance : (instance = new StreamErrorSerializer());
	}

	@Override
	public Document serialize(DataElementInterface element) {
		if (!(element instanceof StreamErrorElement)) {
			throw new IllegalArgumentException();
		}
		StreamErrorElement streamErrorElement = (StreamErrorElement) element;
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement(ERROR);
		root.addElement(streamErrorElement.getCondition().getName());
		Element text = root.addElement(TEXT);
		text.addText(streamErrorElement.getMessage());

		return document;
	}

}
