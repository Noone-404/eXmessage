package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.Negotiation.*;
import org.dom4j.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class NegotiationFeatureSetSerializerTest {

	private NegotiationFeatureSetSerializerInterface serializer;

	@Before
	public void setUp() throws Exception {
		this.serializer = NegotiationFeatureSetSerializer.getInstance();
		NegotiationFeature.setNegotiationFeatureSerializer(NegotiationFeatureSerializer.getInstance());
	}

	@After
	public void tearDown() throws Exception {
		this.serializer = null;
	}

	@Test
	public void serialize() throws Exception {
		Collection<NegotiationFeatureOptionInterface> options    = new ArrayList<>();
		Collection<String>                            mechanisms = new ArrayList<>();
		mechanisms.add("simple-Password-Authentication");
		NegotiationFeatureOptionInterface mechanismOption = new NegotiationFeatureOption("mechanism", mechanisms);
		options.add(mechanismOption);
		NegotiationFeatureInterface             negotiationFeature = new NegotiationFeature("simple-Authentication", options, true);
		Collection<NegotiationFeatureInterface> features           = new ArrayList<>();
		features.add(negotiationFeature);
		NegotiationFeatureSetInterface featureSet        = new NegotiationFeatureSet(features);
		Document                       resultingDocument = this.serializer.serialize(featureSet);
		assertEquals("<stream-features><simple-Authentication><required/><mechanism>simple-Password-Authentication</mechanism></simple-Authentication></stream-features>",
		             resultingDocument.getRootElement().asXML());
	}

	@Test
	public void serializeEmptySet() throws Exception {
		Collection<NegotiationFeatureInterface> features = new ArrayList<>();
		NegotiationFeatureSetInterface featureSet = new NegotiationFeatureSet(features);
		Document resultingDocument = this.serializer.serialize(featureSet);
		assertEquals("<stream-features/>", resultingDocument.getRootElement().asXML());
	}

}
