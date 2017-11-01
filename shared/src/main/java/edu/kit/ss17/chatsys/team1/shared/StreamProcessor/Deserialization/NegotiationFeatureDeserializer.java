package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeature;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureOption;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureOptionInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import org.dom4j.Branch;
import org.dom4j.Node;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class deserializes NegotiationFeatures. It should only be called from a NegotiationFeatureSetDeserializer.
 */
public class NegotiationFeatureDeserializer implements DeserializerInterface {

	private static final String REQUIRED = "required";

	NegotiationFeatureDeserializer() {
	}

	@Nullable
	@Override
	public DataElementInterface deserializeXML(Branch root) throws StanzaErrorException {
		NegotiationFeatureInterface                    negotiationFeature = null;
		String                                         name               = root.getName();
		boolean                                        required           = false;
		Collection<NegotiationFeatureOptionInterface>  options            = new ArrayList<>();
		Map<String, NegotiationFeatureOptionInterface> hashMap            = new HashMap<>();
		for (final Node child : (Collection<Node>) root.content()) {
			if (child.getName().equals(REQUIRED)) {
				required = true;
				continue;
			} else if (hashMap.get(child.getName()) != null) {
				hashMap.get(child.getName()).addValue(child.getText());
				continue;
			}
			Collection<String> values = new ArrayList<>();
			values.add(child.getText());
			NegotiationFeatureOptionInterface option = new NegotiationFeatureOption(child.getName(), values);
			options.add(option);
			hashMap.put(option.getOptionName(), option);
		}
		negotiationFeature = new NegotiationFeature(name, options, required);

		return negotiationFeature;
	}
}
