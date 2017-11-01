package edu.kit.ss17.chatsys.team1.shared.Negotiation;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;

import java.util.Collection;

/**
 *
 */
public interface NegotiationFeatureInterface extends DataElementInterface {

	/**
	 * return the name of this Feature
	 */
	String getName();

	/**
	 * returns all supported options for this feature
	 */
	Collection<NegotiationFeatureOptionInterface> getOptions();

	/**
	 * indicates whether this feature is required to negotiate
	 *
	 * @return
	 */
	boolean isRequired();
}
