package edu.kit.ss17.chatsys.team1.shared.Util.Account;

import edu.kit.ss17.chatsys.team1.shared.Util.JID;

import javax.persistence.*;

/**
 * General representation of an account with password.
 */
@Entity
@Table(name = "account")
public class Account implements AccountInterface {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;

	@Embedded
	private JID jid;

	@Column(name = "password")
	private String password;

	/**
	 * Creates an (invalid) account without JID nor password.
	 */
	public Account() {
		this.jid = null;
		this.password = "";
	}

	/**
	 * Creates an account with JID and password.
	 *
	 * @param jid      the JID
	 * @param password the password
	 */
	public Account(JID jid, String password) {
		this.jid = jid;
		this.password = password;
	}

	@Override
	public int getID() {
		return this.id;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	@Override
	public JID getJid() {
		return this.jid;
	}

	@Override
	public void setJid(JID jid) {
		this.jid = jid;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int hashCode() {
		return (this.jid.getFullJID() + this.password).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Account))
			return false;

		Account acc = (Account) obj;
		return this.getJid().equals(acc.getJid()) && this.getPassword().equals(acc.getPassword()) && this.getID() == acc.getID();
	}
}
