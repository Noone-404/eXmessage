package edu.kit.ss17.chatsys.team1.shared.Util.Account;

import edu.kit.ss17.chatsys.team1.shared.Util.JID;

/**
 * General representation of an account with password.
 */
public interface AccountInterface {

	/**
	 * Getter for ID.
	 *
	 * @return the ID
	 */
	int getID();

	/**
	 * Setter for ID.
	 *
	 * @param id the id
	 */
	void setID(int id);

	/**
	 * Getter for the accounts JID.
	 *
	 * @return the JID
	 */
	JID getJid();

	/**
	 * Setter for the accounts JID.
	 *
	 * @param jid the JID
	 */
	void setJid(JID jid);

	/**
	 * Getter for password.
	 *
	 * @return the password
	 */
	String getPassword();

	/**
	 * Setter for password.
	 *
	 * @param password the password
	 */
	void setPassword(String password);
}
