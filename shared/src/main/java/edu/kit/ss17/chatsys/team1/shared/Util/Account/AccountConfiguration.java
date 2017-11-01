package edu.kit.ss17.chatsys.team1.shared.Util.Account;

import javax.persistence.*;

/**
 * Representation of an account configuration.
 */
@Entity
@Table(name = "accountConfiguration")
public class AccountConfiguration implements AccountConfigurationInterface {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;

	@OneToOne(cascade = CascadeType.ALL, targetEntity = Account.class)
	private AccountInterface account;

	@Column(name = "protocolName")
	private String protocolName;

	@Column(name = "address")
	private String address;

	@Column(name = "port")
	private int port;


	/**
	 * Default constructor for hibernate.
	 */
	public AccountConfiguration() {
	}


	/**
	 * Creates an AccountConfiguration with a given account, protocol name, host and port.
	 *
	 * @param account      the account
	 * @param protocolName the protocol name
	 * @param address      the ip address
	 * @param port         the servers port
	 */
	public AccountConfiguration(AccountInterface account, String protocolName, String address, int port) {
		this.account = account;
		this.protocolName = protocolName;
		this.address = address;
		this.port = port;
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
	public AccountInterface getAccount() {
		return this.account;
	}

	@Override
	public void setAccount(AccountInterface account) {
		this.account = account;
	}

	@Override
	public String getProtocolName() {
		return this.protocolName;
	}

	@Override
	public void setProtocolName(String name) {
		this.protocolName = name;
	}

	@Override
	public String getAddress() {
		return this.address;
	}

	@Override
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public int getPort() {
		return this.port;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public int hashCode() {
		return (this.protocolName + this.address + this.port + this.account.getJid() + this.account.getPassword()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		AccountConfiguration other = (AccountConfiguration) obj;

		if (this.id != other.id)
			return false;
		if (this.port != other.port)
			return false;
		if (this.account != null ? !this.account.equals(other.account) : other.account != null)
			return false;
		if (this.protocolName != null ? !this.protocolName.equals(other.protocolName) : other.protocolName != null)
			return false;
		return this.address != null ? this.address.equals(other.address) : other.address == null;
	}
}
