package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.IQInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Iterator;

/**
 * This class translates an IQ object into a {@link Document}.
 */
public final class IQSerializer extends AbstractStanzaSerializer implements IQSerializerInterface {

	private static final String IQ = "iq", CONTENT = "content";
	private static IQSerializerInterface instance;

	/**
	 * Private constructor to make Serializers Singletons.
	 */
	private IQSerializer() {
	}

	/**
	 * @return this Singleton's instance.
	 */
	public static IQSerializerInterface getInstance() {
		return (instance != null) ? instance : (instance = new IQSerializer());
	}

	@Override
	public Document serialize(DataElementInterface element) {
		if (!(element instanceof IQInterface)) {
			throw new IllegalArgumentException();
		}
		IQInterface iq       = (IQInterface) element;
		Document    document = DocumentHelper.createDocument();
		Element     root     = document.addElement(IQ);
		root = serializeMetadata(root, iq);
		Element          child  = root.addElement(iq.getRequest());
		Iterator<String> names  = iq.getParameterNames().iterator();
		Iterator<String> values = iq.getParameterValues().iterator();
		while (names.hasNext()) {
			child.addAttribute(names.next(), values.next());
		}
		if (!iq.getDefaultContent().isEmpty() || iq.getContent() != null) {
			Element content = child.addElement(CONTENT);
			for (String defaultContent : iq.getDefaultContent()) {
				content.addText("\n" + defaultContent);
			}
			if (iq.getContent() != null) {
				content.add(iq.getContent().serialize());
			}
		}

		return document;
	}

}
