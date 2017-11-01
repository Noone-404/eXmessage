package edu.kit.ss17.chatsys.team1.shared.Negotiation;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.NegotiationFeatureSetSerializerInterface;
import org.dom4j.Document;

import java.util.Collection;

/**
 * Class to represent a set of negotiation Features to be sent in one step of the stream negotiation
 */
public class NegotiationFeatureSet implements NegotiationFeatureSetInterface {

	private static NegotiationFeatureSetSerializerInterface negotiationFeatureSetSerializer;

	private Collection<NegotiationFeatureInterface> features;

	public NegotiationFeatureSet(Collection<NegotiationFeatureInterface> features) {
		this.features = features;
	}

	public static NegotiationFeatureSetSerializerInterface getNegotiationFeatureSetSerializer() {
		return negotiationFeatureSetSerializer;
	}

	public static void setNegotiationFeatureSetSerializer(NegotiationFeatureSetSerializerInterface negotiationFeatureSetSerializer) {
		NegotiationFeatureSet.negotiationFeatureSetSerializer = negotiationFeatureSetSerializer;
	}

	@Override
	public Collection<NegotiationFeatureInterface> getFeatures() {
		return this.features;
	}

	@Override
	public Document serialize() {
		return negotiationFeatureSetSerializer.serialize(this);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NegotiationFeatureSet) {
			NegotiationFeatureSet negotiationFeatureSet = (NegotiationFeatureSet) o;
			return this.features.containsAll(negotiationFeatureSet.getFeatures()) && negotiationFeatureSet.getFeatures().containsAll(this.features);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime  = 31;
		int       result = 1;
		for (final NegotiationFeatureInterface feature : this.features) {
			result = result * prime + feature.hashCode();
		}
		return result;
	}

	@Override
	public String toString() {
		return "NegotiationFeatureSet{" +
		       "features=" + features +
		       '}';
	}
}
