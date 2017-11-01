package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeature;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class NegotiationFeatureDeserializerTest {

	NegotiationFeatureDeserializer negotiationFeatureDeserializer;

	@Before
	public void setUp() throws Exception {
		negotiationFeatureDeserializer = new NegotiationFeatureDeserializer();
	}

	@After
	public void tearDown() throws Exception {
		negotiationFeatureDeserializer = null;
	}

	@Test
	public void deserializeXML() throws Exception {
		Document           document           = DocumentHelper
				.parseText("<simple-Authentication><required/><mechanism>simple-Password-Authentication</mechanism></simple-Authentication>");
		NegotiationFeature negotiationFeature = (NegotiationFeature) negotiationFeatureDeserializer.deserializeXML(document.getRootElement()); // because of weird dom4j structure
		assertTrue("Wrong feature name.", "simple-Authentication".equals(negotiationFeature.getName()));
		assertTrue("Wrong required value.", negotiationFeature.isRequired());
		assertTrue("Wrong option name.", "mechanism".equals(negotiationFeature.getOptions().iterator().next().getOptionName()));
		assertTrue("Wrong option value.", "simple-Password-Authentication".equals(negotiationFeature.getOptions().iterator().next().getValues().iterator().next()));
	}

}
