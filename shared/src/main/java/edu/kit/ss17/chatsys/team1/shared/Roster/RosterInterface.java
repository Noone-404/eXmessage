package edu.kit.ss17.chatsys.team1.shared.Roster;

import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObservableInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Interface for roster. Holds contacts.
 */
public interface RosterInterface extends ObservableInterface<RosterObserverInterface> {

	int getId();

	void setId(int id);

	/**
	 * Get the contact list.
	 *
	 * @return Collection of contacts.
	 */
	Collection<ContactInterface> getContacts();

	AccountInterface getAccount();

	void setAccount(AccountInterface account);

	/**
	 * Sets all contacts at once. Needed by hibernate.
	 *
	 * @param contacts the contacts
	 */
	void setContacts(Collection<ContactInterface> contacts);

	/**
	 * Creates and optionally saves a new contact
	 *
	 * @param contactJID The contacts JID
	 * @param alias      The contacts alias name
	 * @param persistent Determines if the contact should persist
	 *
	 * @return The newly created contact
	 */
	ContactInterface createContact(JID contactJID, String alias, boolean persistent);

	/**
	 * Deletes a contact.
	 *
	 * @param jid The contact's jid
	 */
	void deleteContact(JID jid);

	/**
	 * @param jid The contacts JID
	 *
	 * @return The contact with the provided jid or null, if no matching contact is found
	 */
	@Nullable
	ContactInterface getContact(JID jid);
}
