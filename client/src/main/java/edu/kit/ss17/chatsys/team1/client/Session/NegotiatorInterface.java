package edu.kit.ss17.chatsys.team1.client.Session;

import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiatorBaseInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.jetbrains.annotations.Nullable;

/**
 * A client-side negotiator.
 */
public interface NegotiatorInterface extends NegotiatorBaseInterface {

	/**
	 * Starts negotiation of the feature.
	 *
	 * @param feature Feature element sent by the server
	 *
	 * @return negotiation data to send to the server
	 */
	@Nullable
	DataElementInterface startNegotiation(NegotiationFeatureInterface feature);
}
