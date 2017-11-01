package edu.kit.ss17.chatsys.team1.shared.Negotiation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 *
 */
public class NegotiationFeatureSetTest {

	@Test
	public void getFeatures() throws Exception {
		Collection<NegotiationFeatureInterface>       features   = new LinkedList<>();
		Collection<NegotiationFeatureOptionInterface> options    = new ArrayList<>();
		Collection<String>                            mechanisms = new ArrayList<>();
		mechanisms.add("simple-Password-Authentication");
		NegotiationFeatureOptionInterface mechanismOption = new NegotiationFeatureOption("mechanism", mechanisms);
		options.add(mechanismOption);
		features.add(new NegotiationFeature("simple-Authentication", options, true));
		NegotiationFeatureSetInterface negotiationFeatureSet = new NegotiationFeatureSet(features);
		assertEquals(features, negotiationFeatureSet.getFeatures());
	}

	@Test
	public void equals() throws Exception {
		Collection<NegotiationFeatureInterface>       features   = new LinkedList<>();
		Collection<NegotiationFeatureOptionInterface> options    = new ArrayList<>();
		Collection<String>                            mechanisms = new ArrayList<>();
		mechanisms.add("simple-Password-Authentication");
		NegotiationFeatureOptionInterface mechanismOption = new NegotiationFeatureOption("mechanism", mechanisms);
		options.add(mechanismOption);
		features.add(new NegotiationFeature("simple-Authentication", options, true));
		NegotiationFeatureSetInterface          negotiationFeatureSet = new NegotiationFeatureSet(features);
		NegotiationFeatureSet                   differentSet          = new NegotiationFeatureSet(new LinkedList<>());
		Collection<NegotiationFeatureInterface> sameFeatures          = new HashSet<>();
		sameFeatures.addAll(features);
		NegotiationFeatureSet identicalSet = new NegotiationFeatureSet(sameFeatures);
		assertTrue(negotiationFeatureSet.equals(negotiationFeatureSet));
		assertTrue(negotiationFeatureSet.equals(identicalSet));
		assertFalse(negotiationFeatureSet.equals(differentSet));
	}

}
