package edu.kit.ss17.chatsys.team1.client.AccountManager;

import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;

import java.util.Collection;

/**
 * Component, which is responsible for retrieving account configurations from and storing to the DB.
 */
public interface AccountManagerInterface {

	/**
	 * Get a list of account configurations.
	 */
	Collection<AccountConfigurationInterface> getAccountList();

	/**
	 * Save a new or existing account configuration.
	 */
	void saveAccount(AccountConfigurationInterface account);

	/**
	 * Deletes a existing account configuration
	 *
	 * @param account The account to be deleted
	 */
	void deleteAccount(AccountConfigurationInterface account);

	/**
	 * Set the storage to access our DB.
	 */
	void setStorage(StorageInterface storage);
}
