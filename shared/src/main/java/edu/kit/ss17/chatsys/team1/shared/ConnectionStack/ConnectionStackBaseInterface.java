package edu.kit.ss17.chatsys.team1.shared.ConnectionStack;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainUpperDataProcessorInterface;

/**
 * Base interface for a @code{ConnectionStack}, used on client and server.
 */
public interface ConnectionStackBaseInterface extends ChainUpperDataProcessorInterface<DataElementInterface> {

	/**
	 * Called when the connection stack should be destroyed.
	 */
	void destroy();

	/**
	 * Called by the Session once the negotiation is finished.
	 *
	 * @param success whether or not the negotiation was successfull.
	 */
	void negotiationFinished(boolean success);
}
