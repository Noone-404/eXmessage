package edu.kit.ss17.chatsys.team1.shared.StreamData;

import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObserverInterface;

/**
 * Observes a message.
 */
public interface MessageObserverInterface extends ObserverInterface {

	/**
	 * Notifies the observer about a (unknown) change.
	 */
	void messageChanged(MessageInterface message);
}
