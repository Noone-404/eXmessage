package edu.kit.ss17.chatsys.team1.shared.Roster;

import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObserverInterface;

/**
 *
 */
public interface MessageObserverInterface extends ObserverInterface {

	/**
	 * Notifies observers about a received message.
	 */
	void messageAdded(ContactInterface contact, MessageInterface message);

	/**
	 * Notifies observers about a changed message, e.g., status by a plugin.
	 */
	void messageChanged(ContactInterface contact, MessageInterface message);
}
