package edu.kit.ss17.chatsys.team1.client.Model;

import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.MessageObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValue;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValueInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 * Client side implementation of the ContactInterface.
 */
@Entity
@Table(name = "contact")
public class Contact implements ContactInterface {

	private static final Logger logger = LogManager.getLogger(APP_NAME);

	private Collection<MessageObserverInterface> observers;

	private int             id;
	private JID             jid;
	private RosterInterface roster;

	private Property<PresenceValueInterface> presence;
	private Property<String>                 aliasProperty;
	private Property<Boolean>                persistenceProperty;

	public Contact() {
		this.observers = new ArrayList<>();
		this.presence = new SimpleObjectProperty<>(PresenceValue.OFFLINE);
		this.aliasProperty = new SimpleStringProperty("");
		this.persistenceProperty = new SimpleBooleanProperty(false);
	}

	/**
	 * Creates a contact with the given jid, alias and persistence.
	 *
	 * @param jid        the jid
	 * @param alias      the contacts alias as shown in the roster
	 * @param persistent indicates whether this contact should be saved permanently
	 */
	public Contact(JID jid, String alias, boolean persistent, RosterInterface roster) {
		this();

		this.jid = jid;
		if (alias != null)
			this.aliasProperty.setValue(alias);
		if (persistent)
			this.persistenceProperty.setValue(true);
		this.roster = roster;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public int getID() {
		return this.id;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	@Override
	@ManyToOne(cascade = CascadeType.ALL, targetEntity = Roster.class)
	@OnDelete(action = OnDeleteAction.CASCADE)
	public RosterInterface getRoster() {
		return this.roster;
	}

	@Override
	public void setRoster(RosterInterface roster) {
		this.roster = roster;
	}

	@Override
	@Embedded
	public JID getJid() {
		return this.jid;
	}

	@Override
	public void setJid(JID jid) {
		this.jid = jid;
	}

	@Override
	@Column(name = "alias")
	public String getAlias() {
		return this.aliasProperty.getValue();
	}

	@Override
	public void setAlias(String alias) {
		logger.trace("Setting alias in " + System.identityHashCode(this));
		this.aliasProperty.setValue((alias == null) ? "" : alias);
	}

	@Override
	@Transient
	public boolean isPersistent() {
		return this.persistenceProperty.getValue();
	}

	@Override
	@Transient
	public Property<PresenceValueInterface> presenceProperty() {
		return this.presence;
	}

	@Override
	@Transient
	public Property<String> aliasProperty() {
		return this.aliasProperty;
	}

	@Override
	public Property<Boolean> persistenceProperty() {
		return this.persistenceProperty;
	}

	@Override
	public void makePersistent() {
		if (this.persistenceProperty.getValue())
			return;

		this.persistenceProperty.setValue(true);
	}

	@Override
	public void addMessage(AccountConfigurationInterface account, MessageInterface message) {
		if (MessageBagFactory.getBagFor(account, this).getMessage(message.getID()) != null) {
			logger.trace("Contact: not adding message " + message.getID() + " as it already exists in bag.");
			return;
		}

		logger.trace("Contact: adding message " + message.getID());
		message.registerObserver(this::onMessageChanged);
		MessageBagFactory.getBagFor(account, this).addMessage(message);
		onMessageAdded(message);
	}

	@Override
	@Transient
	public Collection<MessageInterface> getMessages(AccountConfigurationInterface account) {
		return MessageBagFactory.getBagFor(account, this).getMessages();
	}

	protected void onMessageAdded(MessageInterface message) {
		this.observers.forEach(messageObserverInterface -> messageObserverInterface.messageAdded(this, message));
	}

	protected void onMessageChanged(MessageInterface message) {
		this.observers.forEach(messageObserverInterface -> messageObserverInterface.messageChanged(this, message));
	}

	@Override
	public void registerObserver(MessageObserverInterface observer) {
		if (!this.observers.contains(observer))
			this.observers.add(observer);
	}

	@Override
	public void unregisterObserver(MessageObserverInterface observer) {
		if (this.observers.contains(observer))
			this.observers.remove(observer);
	}

	@Override
	public String toString() {
		if (this.jid == null)
			return super.toString();

		return this.jid.toString() + (this.getAlias().isEmpty() ? "" : " (" + this.getAlias() + ')');
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		Contact contact = (Contact) obj;

		if (this.id != contact.id)
			return false;
		if (!this.jid.equals(contact.jid))
			return false;
		return this.getAlias().equals(contact.getAlias());
	}

	@Override
	public int hashCode() {
		int result = this.id;
		result = 31 * result + this.jid.hashCode();
		result = 31 * result + this.getAlias().hashCode();
		return result;
	}
}
