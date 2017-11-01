package edu.kit.ss17.chatsys.team1.shared.Negotiation;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.jetbrains.annotations.Nullable;

/**
 * A @code{NegotiatorBaseInterface} instance negotiates a single feature for the @code{Session}.
 */
public interface NegotiatorBaseInterface {

	/**
	 * Performs the next step for negotiating a feature.
	 *
	 * @param input negotiation data sent by the client
	 *
	 * @return next negotiation data to be sent to the client
	 */
	@Nullable
	DataElementInterface negotiate(DataElementInterface input);

	/**
	 * Indicates whether negotiation of the feature has finished.
	 *
	 * @return if the negotiation of this feature has finished
	 */
	boolean isNegotiationFinished();

	/**
	 * Indicates whether the negotiation of this feature has failed.
	 *
	 * @return if the negotiation of this feature failed
	 */
	boolean hasNegotiationFailed();

}
