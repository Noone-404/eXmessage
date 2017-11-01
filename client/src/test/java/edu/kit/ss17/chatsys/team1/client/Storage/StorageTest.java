package edu.kit.ss17.chatsys.team1.client.Storage;

import edu.kit.ss17.chatsys.team1.client.Model.Roster;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.Account;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfiguration;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 *
 */
public class StorageTest {

	private final Storage storage = Storage.getInstance();


	@Test
	public void setGetActivePluginSets() {
		this.storage.truncateTable("PluginSet");

		assertThat("Must not contain active plugin sets", this.storage.getActivePluginSets(), empty());

		Collection<PluginSetInterface> toBeStored = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			PluginSetInterface psi = mock(PluginSetInterface.class);
			when(psi.getName()).thenReturn("Mocked PSI " + i);
			toBeStored.add(psi);
		}

		this.storage.setActivePluginsSets(toBeStored);

		// Now assert that there's no enabled set. Then retrieve it. Store it as enabled, scan again and then assert that set to be enabled.
		Collection<String> enabledSets = this.storage.getActivePluginSets();
		assertThat("Has to contain 3 elements", enabledSets, hasSize(3));
		assertThat("Has to contain all enabled elements", enabledSets, contains("Mocked PSI 1", "Mocked PSI 2", "Mocked PSI 3"));
	}

	@Test
	public void saveGetLanguage() {
		this.storage.truncateTable("Variable");

		String lang = "de";
		this.storage.saveLanguage(lang);

		String lang2 = this.storage.getLanguage();
		Assert.assertEquals(lang, lang2);
	}

	@Test
	public void saveGetRoster() throws InvalidJIDException {
		this.storage.truncateTable("Contact");
		this.storage.truncateTable("Roster");
		this.storage.truncateTable("AccountConfiguration");
		this.storage.truncateTable("Account");

		JID              jid     = new JID("test@test.de/home");
		AccountInterface account = new Account(jid, "asdf");
		RosterInterface  roster  = new Roster();
		roster.setAccount(account);

		JID jid2 = new JID("contact@contact.de/home");
		roster.createContact(jid2, null, true);

		this.storage.saveRoster(roster);

		RosterInterface roster2 = this.storage.getRoster(account);

		Assert.assertTrue(roster.getId() == roster2.getId());
		assertThat("Contacts of rosters don't equal", roster.getContacts().size(), equalTo(roster2.getContacts().size()));

		// Cleanup
		this.storage.removeAccount(roster.getAccount());

		// Check roster has been deleted
		Assert.assertNull(this.storage.getRoster(account));

		// Check account has been deleted
		Assert.assertNull(this.storage.getAccount(jid));
	}

	@Test
	public void saveGetRosterDifferentResources() throws InvalidJIDException {
		this.storage.truncateTable("Contact");
		this.storage.truncateTable("Roster");
		this.storage.truncateTable("AccountConfiguration");
		this.storage.truncateTable("Account");

		JID              jid     = new JID("test@test.de/home");
		AccountInterface account = new Account(jid, "asdf");
		RosterInterface  roster  = new Roster();
		roster.setAccount(account);

		JID jid2 = new JID("contact@contact.de/home");
		roster.createContact(jid2, null, true);

		this.storage.saveRoster(roster);

		// Create another account object whose JID has another resource. It should get the same roster as with the original resource.
		JID              jid3     = new JID("test@test.de/work");
		AccountInterface account2 = new Account(jid3, "asdf");
		RosterInterface  roster2  = this.storage.getRoster(account2);


		Assert.assertEquals(roster.getId(), roster2.getId());
		assertThat("Contacts of rosters don't equal", roster.getContacts().size(), equalTo(roster2.getContacts().size()));

		// Cleanup
		this.storage.removeAccount(roster.getAccount());

		// Check roster has been deleted
		Assert.assertNull(this.storage.getRoster(account));

		// Check account has been deleted
		Assert.assertNull(this.storage.getAccount(jid));
	}

	@Test
	public void deleteRosterContact() throws InvalidJIDException {
		this.storage.truncateTable("Contact");
		this.storage.truncateTable("Roster");
		this.storage.truncateTable("AccountConfiguration");
		this.storage.truncateTable("Account");

		// Create roster + account
		JID              jid     = new JID("test@test.de/home");
		AccountInterface account = new Account(jid, "asdf");
		RosterInterface  roster  = new Roster();
		roster.setAccount(account);

		// Create contact
		JID jid2 = new JID("contact@contact.de/home");
		roster.createContact(jid2, null, true);

		this.storage.saveRoster(roster);

		assertThat("Roster should have 1 contact", roster.getContacts().size(), equalTo(1));

		roster.deleteContact(jid2);
		this.storage.saveRoster(roster);

		RosterInterface roster2 = this.storage.getRoster(account);

		assertThat("Roster should have 0 contacts", roster2.getContacts().size(), equalTo(0));
	}

	@Test
	public void saveGetAccountConfiguration() throws InvalidJIDException {
		this.storage.truncateTable("AccountConfiguration");
		this.storage.truncateTable("Account");

		JID                  jid     = new JID("test@example.org");
		AccountInterface     account = new Account(jid, "asdf");
		AccountConfiguration cfg     = new AccountConfiguration(account, "tcp", "192.168.2.111", 1234);

		this.storage.saveAccountConfiguration(cfg);

		Collection<AccountConfigurationInterface> configs = this.storage.getAccountConfigurations();
		boolean                                   found   = false;
		for (AccountConfigurationInterface cfg2 : configs) {
			if (cfg2.equals(cfg)) {
				found = true;
				break;
			}
		}

		Assert.assertTrue(found);

		// Cleanup
		this.storage.removeAccountConfiguration(cfg);

		// Check no account configurations are left
		configs = this.storage.getAccountConfigurations();
		Assert.assertTrue(configs.size() == 0);

		// Check no accounts are left
		AccountInterface account2 = this.storage.getAccount(jid);
		Assert.assertNull(account2);
	}

	@Test
	public void saveDeleteAccountConfigurationRoster() throws InvalidJIDException {
		this.storage.truncateTable("Contact");
		this.storage.truncateTable("Roster");
		this.storage.truncateTable("AccountConfiguration");
		this.storage.truncateTable("Account");

		JID                  jid     = new JID("test@example.org");
		AccountInterface     account = new Account(jid, "asdf");
		AccountConfiguration cfg     = new AccountConfiguration(account, "tcp", "192.168.2.111", 1234);

		this.storage.saveAccountConfiguration(cfg);

		RosterInterface roster = new Roster();
		roster.setAccount(cfg.getAccount());
		roster.createContact(new JID("test@test.de"), null, true);
		this.storage.saveRoster(roster);

		this.storage.removeAccountConfiguration(cfg);

		// Check no account configurations are left
		Collection<AccountConfigurationInterface> configs = this.storage.getAccountConfigurations();
		Assert.assertTrue(configs.size() == 0);

		// Check no accounts are left
		AccountInterface account2 = this.storage.getAccount(jid);
		Assert.assertNull(account2);

		// Check no roster is left
		Assert.assertTrue(this.storage.getRoster(cfg.getAccount()) == null);
	}

	@Test
	public void updateAccountConfiguration() throws InvalidJIDException {
		this.storage.truncateTable("AccountConfiguration");
		this.storage.truncateTable("Account");

		JID                  jid     = new JID("test@example.org");
		AccountInterface     account = new Account(jid, "asdf");
		AccountConfiguration cfg     = new AccountConfiguration(account, "tcp", "192.168.2.111", 1234);

		this.storage.saveAccountConfiguration(cfg);

		cfg.setProtocolName("UDP");
		cfg.setPort(5222);
		this.storage.saveAccountConfiguration(cfg);

		Collection<AccountConfigurationInterface> configs = this.storage.getAccountConfigurations();
		boolean                                   found   = false;
		for (AccountConfigurationInterface cfg2 : configs) {
			if (cfg2.equals(cfg)) {
				found = true;
				break;
			}
		}

		assertThat("Updated AccountConfiguration not found", found, equalTo(true));

		// Cleanup
		this.storage.removeAccountConfiguration(cfg);

		// Check no account configurations are left
		configs = this.storage.getAccountConfigurations();
		assertThat("There shouldn't be any AccountConfigurations left at the DB", configs.size(), equalTo(0));
	}

	@Test
	public void saveGetAccount() throws InvalidJIDException {
		this.storage.truncateTable("Roster");
		this.storage.truncateTable("AccountConfiguration");
		this.storage.truncateTable("Account");

		JID              jid     = new JID("test@test.de/home");
		AccountInterface account = new Account(jid, "asdf");

		this.storage.saveAccount(account);

		AccountInterface account2 = this.storage.getAccount(jid);

		Assert.assertTrue(account.equals(account2));

		// Cleanup
		this.storage.removeAccount(account);

		Assert.assertNull(this.storage.getAccount(jid));
	}
}
