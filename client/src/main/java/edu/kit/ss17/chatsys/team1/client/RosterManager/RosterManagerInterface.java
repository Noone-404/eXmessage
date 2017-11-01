package edu.kit.ss17.chatsys.team1.client.RosterManager;

import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;

/**
 * Roster Manager.
 */
public interface RosterManagerInterface {

	/**
	 * Get the existing roster of a given account or new one.
	 */
	RosterInterface getRoster(AccountConfigurationInterface account);

	/**
	 * Save the roster.
	 */
	void saveRoster(RosterInterface roster);

	/**
	 * Tells the Roster Manager which storage to use.
	 */
	void setStorage(StorageInterface storage);
}
