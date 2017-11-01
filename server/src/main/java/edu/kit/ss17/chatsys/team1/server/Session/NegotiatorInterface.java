package edu.kit.ss17.chatsys.team1.server.Session;

import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeature;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiatorBaseInterface;

/**
 * A server-side negotiator.
 */
public interface NegotiatorInterface extends NegotiatorBaseInterface {

	/**
	 * Indicates whether this feature is required for further negotiation.
	 *
	 * @return if this feature is required for further negotiation
	 */
	boolean isRequired();

	/**
	 * Returns the @code{NegotiationFeature} element to be sent to the client for negotiation.
	 *
	 * @return a serializable feature element
	 */
	NegotiationFeature getNegotiationFeature();

	/**
	 * Gets the @code{NegotiationFeature}s priority.
	 *
	 * @return the priority of the feature negotiated by this negotiator
	 */
	int getWeight();
}
