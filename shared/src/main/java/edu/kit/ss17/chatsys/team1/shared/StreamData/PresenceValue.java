package edu.kit.ss17.chatsys.team1.shared.StreamData;

import java.util.ResourceBundle;

/**
 * Represents presence states.
 */
public enum PresenceValue implements PresenceValueInterface {

	 // The possible presence values:

	 /**  offline -- The entity or resource is not connected to the server. */
	 OFFLINE,
	 /**  online -- The entity or resource is connected to the server. */
	 ONLINE,
	 /** away -- The entity or resource is temporarily away. */
	 AWAY,
	 /**  dnd -- The entity or resource is busy (dnd = "Do Not Disturb"). */
	 DND,
	 /** chat -- The entity or resource is actively interested in chatting. */
	 CHAT,
	 /** xa -- The entity or resource is away for an extended period (xa = "eXtended Away"). */
	 XA;

	@Override
	public String getHumanReadableName(ResourceBundle bundle) {
		if (bundle == null)
			return this.name();

		return bundle.getString("presence_" + this.toString().toLowerCase());
	}
}
