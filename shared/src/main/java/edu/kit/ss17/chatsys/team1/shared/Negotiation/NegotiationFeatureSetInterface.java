package edu.kit.ss17.chatsys.team1.shared.Negotiation;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;

import java.util.Collection;

/**
 *
 */
public interface NegotiationFeatureSetInterface extends DataElementInterface {

	Collection<NegotiationFeatureInterface> getFeatures();
}
