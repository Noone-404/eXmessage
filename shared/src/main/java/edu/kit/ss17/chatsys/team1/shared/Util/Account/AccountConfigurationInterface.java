package edu.kit.ss17.chatsys.team1.shared.Util.Account;

/**
 * Representation of an account configuration.
 */
public interface AccountConfigurationInterface {

	/**
	 * Get the account configurations identifier.
	 */
	int getId();

	/**
	 * Set the account configurations identifier.
	 *
	 * @param id the id
	 */
	void setId(int id);

	/**
	 * Get this configuration's account.
	 */
	AccountInterface getAccount();

	/**
	 * Set this configuration's account.
	 *
	 * @param account the account
	 */
	void setAccount(AccountInterface account);

	/**
	 * Get the name of the plugin set containing the network plugin.
	 */
	String getProtocolName();

	/**
	 * Sets the name of the plugin set containing the network plugin.
	 */
	void setProtocolName(String name);

	/**
	 * Get the host to connect to.
	 */
	String getAddress();

	/**
	 * Sets the address to connect to.
	 */
	void setAddress(String address);

	/**
	 * Get the connection port.
	 */
	int getPort();

	/**
	 * Sets toe port to connect to.
	 */
	void setPort(int port);
}
