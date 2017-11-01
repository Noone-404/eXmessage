package edu.kit.ss17.chatsys.team1.client.AccountManager;

import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.Account;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfiguration;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 *
 */
public class AccountManagerTest {

	private AccountManagerInterface accountManager;

	@Before
	public void setUp() {
		this.accountManager = AccountManager.getInstance();
	}

	@After
	public void tearDown() {
		this.accountManager = null;
	}

	@Test
	public void getAccountList() {
		Collection<AccountConfigurationInterface> accounts     = new ArrayList<>();
		AccountConfigurationInterface             firstAccount = new AccountConfiguration();
		accounts.add(firstAccount);
		AccountConfigurationInterface secondAccount = new AccountConfiguration(new Account(), "TCP", "localhost", 5222);
		accounts.add(secondAccount);
		StorageInterface storage = mock(StorageInterface.class);
		when(storage.getAccountConfigurations()).thenReturn(accounts);
		this.accountManager.setStorage(storage);
		assertEquals(accounts, this.accountManager.getAccountList());
	}

	@Test
	public void saveAccount() {
		Collection<AccountConfigurationInterface> accountConfigs     = new ArrayList<>();
		AccountInterface                          firstAccount       = new Account(mock(JID.class), "asdf");
		AccountInterface                          secondAccount      = new Account(mock(JID.class), "qwer");
		AccountConfigurationInterface             firstAccountConfig = new AccountConfiguration(firstAccount, "UDP", "localhost", 5222);
		accountConfigs.add(firstAccountConfig);
		AccountConfigurationInterface secondAccountConfig = new AccountConfiguration(secondAccount, "TCP", "localhost", 5222);
		accountConfigs.add(secondAccountConfig);
		StorageInterface storage = mock(StorageInterface.class);
		when(storage.getAccountConfigurations()).thenReturn(accountConfigs);
		this.accountManager.setStorage(storage);
		this.accountManager.saveAccount(firstAccountConfig);
		verify(storage).saveAccountConfiguration(firstAccountConfig);
		this.accountManager.deleteAccount(firstAccountConfig);
		this.accountManager.saveAccount(secondAccountConfig);
		verify(storage).saveAccountConfiguration(secondAccountConfig);
		this.accountManager.deleteAccount(secondAccountConfig);
	}

	@Test
	public void deleteAccount() {
		Collection<AccountConfigurationInterface> accounts = new ArrayList<>();
		AccountConfigurationInterface             account  = new AccountConfiguration(new Account(), "TCP", "localhost", 5222);
		accounts.add(account);
		StorageInterface storage = mock(StorageInterface.class);
		when(storage.getAccountConfigurations()).thenReturn(accounts);
		this.accountManager.setStorage(storage);
		this.accountManager.deleteAccount(account);
		verify(storage).removeAccountConfiguration(account);
	}

}
