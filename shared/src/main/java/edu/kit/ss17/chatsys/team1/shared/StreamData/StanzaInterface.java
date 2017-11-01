package edu.kit.ss17.chatsys.team1.shared.StreamData;

import java.time.Instant;

/**
 * This interface declares the functionality of all stanza objects.
 */
public interface StanzaInterface extends DataElementInterface {

	/**
	 * @return String the stanza's ID
	 */
	String getID();

	/**
	 * Sets the id
	 *
	 * @param id The new id
	 */
	void setID(String id);

	/**
	 * @return String the sender of the Stanza
	 */
	String getSender();

	/**
	 * Sets the sender
	 *
	 * @param sender The new sender
	 */
	void setSender(String sender);

	/**
	 * @return String the receiver
	 */
	String getReceiver();

	/**
	 * Sets the receiver
	 *
	 * @param receiver The new receiver
	 */
	void setReceiver(String receiver);

	/**
	 * @return Date The date + time when the message was sent
	 */
	Instant getClientSendDate();

	/**
	 * Sets the send date
	 *
	 * @param clientSendDate The new send date
	 */
	void setClientSendDate(Instant clientSendDate);

	/**
	 * @return Date The date + time when the message arrived at the server
	 */
	Instant getServerReceiveDate();

	/**
	 * Sets the server receive date
	 *
	 * @param serverReceiveDate The new server receive date
	 */
	void setServerReceiveDate(Instant serverReceiveDate);

	/**
	 * @return Date The date + time when the message arrived at the client
	 */
	Instant getClientReceiveDate();

	/**
	 * Sets the client receive date
	 *
	 * @param clientReceiveDate The new client receive date
	 */
	void setClientReceiveDate(Instant clientReceiveDate);

	/**
	 * @return The stanza type. Note this is not Message, IQ or Presence but the type within such a Stanza group.
	 */
	String getType();

	/**
	 * Sets the stanza type.
	 *
	 * @param newType The stanza type. Note this is not Message, IQ or Presence but the type within such a Stanza group.
	 */
	void setType(String newType);
}
