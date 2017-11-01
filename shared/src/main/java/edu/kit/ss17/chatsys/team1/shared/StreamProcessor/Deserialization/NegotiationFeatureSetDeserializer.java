package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureSet;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class deserializes NegotiationFeatureSets.
 * It uses a {@link NegotiationFeatureDeserializer} for the individual features.
 */
public class NegotiationFeatureSetDeserializer implements DeserializerInterface {

	private final NegotiationFeatureDeserializer negotiationFeatureDeserializer = new NegotiationFeatureDeserializer();
	private static final String STREAM_FEATURES = "stream-features";

	@Nullable
	@Override
	public DataElementInterface deserializeXML(Branch branch) throws StanzaErrorException {
		NegotiationFeatureSet negotiationFeatureSet = null;
		Branch root = ((Document) branch).getRootElement();
		if (root.getName().equals(STREAM_FEATURES)) {
			Collection<NegotiationFeatureInterface> negotiationFeatures = new ArrayList<>();
			for (final Branch child : (Collection<Branch>) root.content()) {
				negotiationFeatures.add((NegotiationFeatureInterface) this.negotiationFeatureDeserializer.deserializeXML(child));
			}
			negotiationFeatureSet = new NegotiationFeatureSet(negotiationFeatures);
		}
		return negotiationFeatureSet;
	}
}
