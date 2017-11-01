package edu.kit.ss17.chatsys.team1.client.GUI.Controller;

import edu.kit.ss17.chatsys.team1.client.Model.ContentInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValueInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObserverInterface;

/**
 * Observes events within the GUI.
 */
public interface GuiObserverInterface extends ObserverInterface {

	/**
	 * Initiate a new connection.
	 *
	 * @param account AccountConfiguration, which holds the connection information.
	 * @param roster the roster of the logged in account
	 */
	default void onConnect(AccountConfigurationInterface account, RosterInterface roster) {
	}

	/**
	 * Close the connection / log off of a given account.
	 *
	 * @param account Account configuration of the affected account.
	 */
	default void onDisconnect(AccountConfigurationInterface account) {
	}

	/**
	 * User entered a message.
	 *
	 * @param account  The account that sends the message.
	 * @param contact The recipient.
	 * @param input    The message.
	 */
	default void onInputComposed(AccountConfigurationInterface account, ContactInterface contact, ContentInterface input) {
	}

	/**
	 * User changed own presence value.
	 */
	default void onPresenceChanged(AccountConfigurationInterface account, PresenceValueInterface presence) {
	}

	/**
	 * User selected another contact from the roster.
	 */
	default void onSelectedContactChanged(ContactInterface contact) {
	}

	/**
	 * The message input field content has changed. This may happen when the user edited the content (e.g. by typing something into the input field) or when a plugin changed the
	 * content tree.
	 */
	default void onInputChanged() {
	}

	/**
	 * Event raised when user performed a contact search request.
	 */
	default void onSearchRequestPerformed(AccountConfigurationInterface account, String searchString) {
	}

	/**
	 * Event raised when non-persistent contact was accepted or new contact was added
	 */
	default void onPersistentContactAdded(AccountConfigurationInterface account, ContactInterface contact) {
	}
}
