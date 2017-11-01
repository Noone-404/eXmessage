package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeature;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureOption;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureOptionInterface;
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
public class NegotiationFeatureSerializerTest {

	NegotiationFeatureSerializerInterface serializer;

	@Before
	public void setUp() throws Exception {
		this.serializer = NegotiationFeatureSerializer.getInstance();
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
		NegotiationFeatureInterface negotiationFeature = new NegotiationFeature("simple-Authentication", options, true);
		Document                    resultingDocument  = this.serializer.serialize(negotiationFeature);
		assertEquals("<simple-Authentication><required/><mechanism>simple-Password-Authentication</mechanism></simple-Authentication>",
		             resultingDocument.getRootElement().asXML());
	}

}
