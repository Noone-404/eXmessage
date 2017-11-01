package edu.kit.ss17.chatsys.team1.client.AccountManager;

import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;

import java.util.Collection;

/**
 * Default account manager implementation.
 */
public final class AccountManager implements AccountManagerInterface {

	private static AccountManager   instance;
	private        StorageInterface storage;

	private AccountManager() {
	}

	public static AccountManager getInstance() {
		return (instance != null) ? instance : (instance = new AccountManager());
	}

	@Override
	public Collection<AccountConfigurationInterface> getAccountList() {
		return this.storage.getAccountConfigurations();
	}

	@Override
	public void saveAccount(AccountConfigurationInterface account) {
		this.storage.saveAccountConfiguration(account);
	}

	@Override
	public void deleteAccount(AccountConfigurationInterface account) {
		for (final AccountConfigurationInterface currentAccount : this.storage.getAccountConfigurations()) {
			if (currentAccount.getId() == account.getId()) {
				this.storage.removeAccountConfiguration(currentAccount);
				break;
			}
		}
	}

	@Override
	public void setStorage(final StorageInterface storage) {
		this.storage = storage;
	}
}
