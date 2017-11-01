package edu.kit.ss17.chatsys.team1.client.RosterManager;

import edu.kit.ss17.chatsys.team1.client.Model.Roster;
import edu.kit.ss17.chatsys.team1.client.Storage.Storage;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.Account;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfiguration;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class RosterManagerTest {

	@Test
	public void saveGetRoster() throws Exception {
		Storage storage = Storage.getInstance();
		storage.truncateTable("Contact");
		storage.truncateTable("AccountConfiguration");
		storage.truncateTable("Roster");
		storage.truncateTable("Account");

		RosterManager rm = RosterManager.getInstance();
		rm.setStorage(storage);

		Roster  roster  = new Roster();
		JID     jid     = new JID("test@example.org/home");
		Account account = new Account(jid, "asdf");
		roster.setAccount(account);

		AccountConfiguration config = new AccountConfiguration(account, "TCP", "192.168.2.111", 1234);

		rm.saveRoster(roster);

		Assert.assertEquals(roster, rm.getRoster(config));

		// Create another roster and assert it's different
		Roster  roster2  = new Roster();
		JID     jid2     = new JID("test2@example.org/work");
		Account account2 = new Account(jid2, "asdf");
		roster2.setAccount(account2);
		Assert.assertNotEquals(roster2, rm.getRoster(config));
	}
}
