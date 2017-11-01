package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeature;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureOptionInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Default serializer for Negotiation Features
 */
public final class NegotiationFeatureSerializer implements NegotiationFeatureSerializerInterface {

	private static final String REQUIRED = "required";
	private static NegotiationFeatureSerializerInterface instance;

	/**
	 * Private constructor to make Serializers Singletons.
	 */
	private NegotiationFeatureSerializer() {
	}

	/**
	 * @return this Singleton's instance.
	 */
	public static NegotiationFeatureSerializerInterface getInstance() {
		return (instance != null) ? instance : (instance = new NegotiationFeatureSerializer());
	}

	@Override
	public Document serialize(DataElementInterface element) {
		if (!(element instanceof NegotiationFeature)) {
			throw new IllegalArgumentException();
		}
		NegotiationFeatureInterface negotiationFeature = (NegotiationFeatureInterface) element;
		Document                    document           = DocumentHelper.createDocument();
		Element                     root               = document.addElement(negotiationFeature.getName());
		if (negotiationFeature.isRequired()) {
			root.addElement(REQUIRED);
		}
		for (final NegotiationFeatureOptionInterface negotiationFeatureOption : negotiationFeature.getOptions()) {
			for (final String value : negotiationFeatureOption.getValues()) {
				Element option = root.addElement(negotiationFeatureOption.getOptionName());
				option.addText(value);
			}
		}

		return document;
	}

}
