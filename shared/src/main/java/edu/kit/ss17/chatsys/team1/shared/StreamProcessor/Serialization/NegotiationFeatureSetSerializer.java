package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureSetInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Default serializer for Negotiation Feature Sets
 */
public final class NegotiationFeatureSetSerializer implements NegotiationFeatureSetSerializerInterface {

	private static final String STREAM_FEATURES = "stream-features";
	private static NegotiationFeatureSetSerializerInterface instance;

	/**
	 * Private constructor to make Serializers Singletons.
	 */
	private NegotiationFeatureSetSerializer() {
	}

	/**
	 * @return this Singleton's instance.
	 */
	public static NegotiationFeatureSetSerializerInterface getInstance() {
		return (instance != null) ? instance : (instance = new NegotiationFeatureSetSerializer());
	}

	@Override
	public Document serialize(DataElementInterface element) {
		if (!(element instanceof NegotiationFeatureSetInterface)) {
			throw new IllegalArgumentException();
		}
		NegotiationFeatureSetInterface negotiationFeatureSet = (NegotiationFeatureSetInterface) element;
		Document                       document              = DocumentHelper.createDocument();
		Element                        root                  = document.addElement(STREAM_FEATURES);
		for (final NegotiationFeatureInterface negotiationFeature : negotiationFeatureSet.getFeatures()) {
			root.add(negotiationFeature.serialize().getRootElement());
		}

		return document;
	}
}
