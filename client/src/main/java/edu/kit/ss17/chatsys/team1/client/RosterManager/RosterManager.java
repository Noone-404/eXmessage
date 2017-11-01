package edu.kit.ss17.chatsys.team1.client.RosterManager;

import edu.kit.ss17.chatsys.team1.client.Model.Roster;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 * Default Roster Manager.
 */
public class RosterManager implements RosterManagerInterface {

	private static final Logger logger = LogManager.getLogger(APP_NAME);

	private static RosterManager    instance;
	private        StorageInterface storage;

	private Map<AccountConfigurationInterface, RosterInterface> rosterCache;

	private RosterManager() {
		this.rosterCache = new ConcurrentHashMap<>(new IdentityHashMap<>());
	}

	public static RosterManager getInstance() {
		return instance != null ? instance : (instance = new RosterManager());
	}

	@Override
	public RosterInterface getRoster(AccountConfigurationInterface account) {
		logger.trace("Roster manager: Fetching roster for account " + System.identityHashCode(account) + ". Checking cache...");
		RosterInterface roster = this.rosterCache.computeIfAbsent(account, key -> {
			logger.trace("Roster manager: No entry found in cache. Fetching roster from storage");
			return this.storage.getRoster(account.getAccount());
		});

		if (roster == null) {
			roster = new Roster();
			logger.trace("Roster manager: No entry found in storage. Creating new roster");
			roster.setAccount(account.getAccount());

			this.storage.saveRoster(roster);
		}
		logger.trace("Roster manager: returning roster " + System.identityHashCode(roster));
		return roster;
	}

	@Override
	public void saveRoster(RosterInterface roster) {
		logger.trace("Saving roster " + System.identityHashCode(roster));
		this.storage.saveRoster(roster);
	}

	@Override
	public void setStorage(StorageInterface storage) {
		this.storage = storage;
	}
}
