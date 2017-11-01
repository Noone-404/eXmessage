package edu.kit.ss17.chatsys.team1.shared.Negotiation;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.NegotiationFeatureSerializerInterface;
import org.dom4j.Document;

import java.util.Collection;

/**
 * Class to represent a single negotiation Feature for stream negotiation
 */
public class NegotiationFeature implements NegotiationFeatureInterface {

	private static NegotiationFeatureSerializerInterface negotiationFeatureSerializer;

	private final String                                        name;
	private final Collection<NegotiationFeatureOptionInterface> options;
	private final boolean                                       required;

	public NegotiationFeature(String name, Collection<NegotiationFeatureOptionInterface> options, boolean required) {
		this.name = name;
		this.options = options;
		this.required = required;
	}

	public static NegotiationFeatureSerializerInterface getNegotiationFeatureSerializer() {
		return negotiationFeatureSerializer;
	}

	public static void setNegotiationFeatureSerializer(
			NegotiationFeatureSerializerInterface negotiationFeatureSerializer) {
		NegotiationFeature.negotiationFeatureSerializer = negotiationFeatureSerializer;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Collection<NegotiationFeatureOptionInterface> getOptions() {
		return this.options;
	}

	@Override
	public boolean isRequired() {
		return this.required;
	}

	@Override
	public Document serialize() {
		return negotiationFeatureSerializer.serialize(this);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NegotiationFeature) {
			NegotiationFeature negotiationFeature = (NegotiationFeature) o;
			return this.getName().equals(negotiationFeature.getName()) && this.isRequired() == negotiationFeature.isRequired()
			       && this.getOptions().containsAll(negotiationFeature.getOptions()) && negotiationFeature.getOptions().containsAll(this.getOptions());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime  = 31;
		int       result = 1;
		result = result * prime + this.getName().hashCode();
		result = result * prime + (this.isRequired() ? 0 : 1);
		for (final NegotiationFeatureOptionInterface option : this.getOptions()) {
			result = result * prime + option.hashCode();
		}
		return result;
	}

	@Override
	public String toString() {
		return "NegotiationFeature{" +
		       "name='" + name + '\'' +
		       ", options=" + options +
		       ", required=" + required +
		       '}';
	}
}
