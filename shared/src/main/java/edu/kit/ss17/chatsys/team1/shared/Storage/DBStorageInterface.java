package edu.kit.ss17.chatsys.team1.shared.Storage;

import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Defines methods to read/write data to/from any dbms.
 */
public interface DBStorageInterface {

	/**
	 * Truncates a table.
	 *
	 * @param table the tables entity name.
	 */
	default void truncateTable(String table) {
	}

	/**
	 * Get a list of active PluginSets.
	 *
	 * @return Collection of Strings.
	 */
	default Collection<String> getActivePluginSets() {
		return null;
	}

	/**
	 * Overrides active PluginSets.
	 *
	 * @param pluginsSets Collection of PluginSets.
	 */
	default void setActivePluginsSets(Collection<PluginSetInterface> pluginsSets) {
	}

	/**
	 * Saves the roster of an account.
	 *
	 * @param roster the roster
	 */
	default void saveRoster(RosterInterface roster) {
	}

	/**
	 * Removes the roster.
	 *
	 * @param roster the roster
	 */
	default void removeRoster(RosterInterface roster) {
	}

	/**
	 * Get the roster of an account.
	 *
	 * @param account The account.
	 */
	default RosterInterface getRoster(AccountInterface account) {
		return null;
	}

	/**
	 * Saves an account configuration.
	 *
	 * @param account the account configuration
	 */
	default void saveAccountConfiguration(AccountConfigurationInterface account) {
	}

	/**
	 * Removes an account configuration.
	 *
	 * @param account the account configuration
	 */
	default void removeAccountConfiguration(AccountConfigurationInterface account) {
	}

	/**
	 * Get a list of all stored account configurations.
	 *
	 * @return the list
	 */
	default Collection<AccountConfigurationInterface> getAccountConfigurations() {
		return null;
	}

	/**
	 * Saves a simple account with JID and password.
	 *
	 * @param account the account
	 */
	void saveAccount(AccountInterface account);

	/**
	 * Removes a simple account with JID and password.
	 *
	 * @param account the account
	 */
	void removeAccount(AccountInterface account);

	/**
	 * Get a simple account with jid and password.
	 */
	@Nullable
	AccountInterface getAccount(JID jid);
}
