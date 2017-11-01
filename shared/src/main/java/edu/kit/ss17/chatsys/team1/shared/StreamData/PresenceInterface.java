package edu.kit.ss17.chatsys.team1.shared.StreamData;

/**
 * PresenceInterface objects represent a client's status.
 */
public interface PresenceInterface extends StanzaInterface {

	/**
	 * Gets the user's current presence value
	 *
	 * @return The current presence value
	 */
	PresenceValueInterface getPresence();

	/**
	 * Sets the presence value that should be represented
	 *
	 * @param newValue The new value
	 */
	void setPresence(PresenceValueInterface newValue);

	/**
	 * @return The status. Note this is not ONLINE, AWAY... but a custom status that the user can set.
	 */
	String getStatus();

	/**
	 * @param newStatus The status. Note this is not ONLINE, AWAY... but a custom status that the user can set.
	 */
	void setStatus(String newStatus);
}
