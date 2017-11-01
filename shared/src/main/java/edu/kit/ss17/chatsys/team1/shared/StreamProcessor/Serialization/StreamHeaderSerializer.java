package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StreamHeaderInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.OpeningTagElement;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Namespace;


/**
 * This class translates a StreamHeader to a {@link Document}.
 */
public final class StreamHeaderSerializer implements StreamHeaderSerializerInterface {

	private static StreamHeaderSerializer instance;

	private StreamHeaderSerializer() {
	}

	/**
	 * @return this Singleton's instance.
	 */
	public static StreamHeaderSerializerInterface getInstance() {
		return (instance != null) ? instance : (instance = new StreamHeaderSerializer());
	}

	@Override
	public Document serialize(DataElementInterface dataElement) {
		if (!(dataElement instanceof StreamHeaderInterface)) {
			throw new IllegalArgumentException();
		}
		StreamHeaderInterface streamHeader      = (StreamHeaderInterface) dataElement;
		OpeningTagElement     openingTagElement = new OpeningTagElement("stream");
		openingTagElement.addAttribute("from", streamHeader.getFrom());
		openingTagElement.addAttribute("id", streamHeader.getStreamID());
		openingTagElement.addAttribute("to", streamHeader.getTo());
		openingTagElement.setNamespace(new Namespace(null, streamHeader.getNamespace()));
		return DocumentHelper.createDocument(openingTagElement);
	}
}
