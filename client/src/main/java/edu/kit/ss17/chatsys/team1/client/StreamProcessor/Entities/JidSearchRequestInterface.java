package edu.kit.ss17.chatsys.team1.client.StreamProcessor.Entities;

import edu.kit.ss17.chatsys.team1.shared.StreamData.IQInterface;

/**
 * An implementing class must represent a JID-search-request-IQ.
 */
public interface JidSearchRequestInterface extends IQInterface {

	/**
	 * Determine whether a response we got matches our request.
	 * @param respose the response we got.
	 * @return true if the response matches our request, false otherwise.
	 */
	boolean isResponseMatching(IQInterface respose);
}
