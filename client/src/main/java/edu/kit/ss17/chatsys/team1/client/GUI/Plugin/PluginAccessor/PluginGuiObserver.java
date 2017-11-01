package edu.kit.ss17.chatsys.team1.client.GUI.Plugin.PluginAccessor;

import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObserverInterface;

/**
 * Observer, which is implemented by plugins and observes events known to the Gui Accessor.
 */
public interface PluginGuiObserver extends ObserverInterface {

	/**
	 * Called if the user selects another contact from his roster and hence, another conversation gets displayed. Also called after connection, when automatically the first
	 * contact
	 * gets selected.
	 */
	void selectedContactChanged(ContactInterface contact);

	/**
	 * Called if something has changed within the input. Used for "User is typing..." like plugins.
	 */
	void inputChanged();
}
