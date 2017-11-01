package edu.kit.ss17.chatsys.team1.shared.Roster;

import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValueInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObservableInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import javafx.beans.property.Property;

import java.util.Collection;

/**
 * Represents a single contact within the roster.
 */
public interface ContactInterface extends ObservableInterface<MessageObserverInterface> {

	/**
	 * We additionally need this id, because otherwise multiple roster could point to the same contact via hibernate.
	 */
	int getID();

	void setID(int id);

	RosterInterface getRoster();

	void setRoster(RosterInterface roster);

	JID getJid();

	void setJid(JID jid);

	String getAlias();

	void setAlias(String alias);

	boolean isPersistent();

	Property<PresenceValueInterface> presenceProperty();
	Property<String> aliasProperty();
	Property<Boolean> persistenceProperty();

	// no setter for false, as we do not need to un-persist contacts
	void makePersistent();

	// default to not force server to implement method.
	default void addMessage(AccountConfigurationInterface account, MessageInterface message) {
	}

	default Collection<MessageInterface> getMessages(AccountConfigurationInterface account) {
		// accesses the message bag to fetch all messages
		return null;
	}
}
