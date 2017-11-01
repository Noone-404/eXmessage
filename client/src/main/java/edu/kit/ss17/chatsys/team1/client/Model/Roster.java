package edu.kit.ss17.chatsys.team1.client.Model;

import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.Account;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.Nullable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 * Default Roster implementation.
 */
@Entity
@Table(name = "roster")
public class Roster implements RosterInterface {

	private static final Logger logger = LogManager.getLogger(APP_NAME);

	@Transient
	private final Collection<RosterObserverInterface> observers = new ArrayList<>();

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;

	@OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, targetEntity = Account.class)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private AccountInterface account;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = Contact.class, orphanRemoval = true)
	private Collection<ContactInterface> contacts = new ArrayList<>();

	/**
	 * Default constructor.
	 */
	public Roster() {
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	@Transient
	public Collection<ContactInterface> getContacts() {
		return this.contacts;
	}

	@Override
	public AccountInterface getAccount() {
		return this.account;
	}

	@Override
	public void setAccount(AccountInterface account) {
		this.account = account;
	}

	@Override
	public void setContacts(Collection<ContactInterface> contacts) {
		this.contacts = contacts;
	}

	@Override
	public ContactInterface createContact(JID contactJID, String alias, boolean persistent) {
		ContactInterface contact = new Contact(contactJID, alias, persistent, this);
		if (!this.contacts.contains(contact))
			this.contacts.add(contact);

		this.observers.forEach(r -> r.contactCreated(this, contact));

		return contact;
	}

	@Override
	public void deleteContact(JID jid) {
		ContactInterface affectedContact = null;

		for (ContactInterface contact : this.contacts) {
			if (contact.getJid().equals(jid)) {
				affectedContact = contact;
				break;
			}
		}

		if (affectedContact != null) {
			logger.trace("Deleting contact " + System.identityHashCode(affectedContact) + " from roster " + System.identityHashCode(this));
			affectedContact.setRoster(null);
			this.contacts.remove(affectedContact);
			final ContactInterface contact = affectedContact;
			this.observers.forEach(r -> r.contactRemoved(this, contact));
		}
	}

	@Nullable
	@Override
	public ContactInterface getContact(JID jid) {
		logger.trace("Checking if '" + jid.getBaseJID() + "' exists in roster " + System.identityHashCode(this));
		for (ContactInterface contact : this.contacts)
			if (contact.getJid().getBaseJID().equals(jid.getBaseJID()))
				return contact;

		return null;
	}

	@Override
	public void registerObserver(RosterObserverInterface observer) {
		if (this.observers.contains(observer)) {
			logger.trace("Roster: Tried to register observer " + System.identityHashCode(observer) +
			             " for roster " + System.identityHashCode(this) + ", but observer was already registered");
		} else {
			logger.trace("Roster: Observer " + System.identityHashCode(observer) + " for roster " + System.identityHashCode(this) + " registered");
			this.observers.add(observer);
			observer.observerRegistered(this);
		}
	}

	@Override
	public void unregisterObserver(RosterObserverInterface observer) {
		if (this.observers.contains(observer)) {
			logger.trace("Roster: Unregistering observer " + System.identityHashCode(observer) + " for roster " + System.identityHashCode(this));
			this.observers.remove(observer);
		} else {
			logger.trace("Roster: Tried to unregister observer " + System.identityHashCode(observer) +
			             " for roster " + System.identityHashCode(this) + ", but observer was not registered");
		}
	}

	@Override
	public int hashCode() {
		return 31 * getAccount().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Roster))
			return false;

		Roster roster = (Roster) obj;
		return this.getAccount().equals(roster.getAccount());
	}
}
