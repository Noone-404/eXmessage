package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureSet;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureSetInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class NegotiationFeatureSetDeserializerTest {

	private static final String FEATURE_NAME = "simple-Authentication", OPTION_NAME = "mechanism", VALUE = "simple-Password-Authentication";
	private NegotiationFeatureSetDeserializer negotiationFeatureSetDeserializer;

	@Before
	public void setUp() throws Exception {
		negotiationFeatureSetDeserializer = new NegotiationFeatureSetDeserializer();
	}

	@After
	public void tearDown() throws Exception {
		negotiationFeatureSetDeserializer = null;
	}

	@Test
	public void deserializeXML() throws Exception {
		Document document = DocumentHelper.parseText("<stream-features><simple-Authentication><required/><mechanism>simple-Password-Authentication</mechanism>" +
		                                             "</simple-Authentication></stream-features>");
		NegotiationFeatureSetInterface negotiationFeatureSet = (NegotiationFeatureSetInterface) negotiationFeatureSetDeserializer.deserializeXML(document);
		assertTrue("Wrong number of negotiation features deserialized", negotiationFeatureSet.getFeatures().size() == 1);
		NegotiationFeatureInterface negotiationFeature = negotiationFeatureSet.getFeatures().iterator().next();
		assertTrue("Wrong feature name.", negotiationFeature.getName().equals(FEATURE_NAME));
		assertTrue("Wrong required value.", negotiationFeature.isRequired());
		assertTrue("Wrong option name", negotiationFeature.getOptions().iterator().next().getOptionName().equals(OPTION_NAME));
		assertTrue("Wrong option value", negotiationFeature.getOptions().iterator().next().getValues().iterator().next().equals(VALUE));
	}

	@Test
	public void deserializeEmptySet() throws Exception {
		Document document = DocumentHelper.parseText("<stream-features/>");
		NegotiationFeatureSetInterface negotiationFeatureSet = (NegotiationFeatureSetInterface) negotiationFeatureSetDeserializer.deserializeXML(document);
		assertTrue("Features should be empty", negotiationFeatureSet.getFeatures().isEmpty());
	}
}
