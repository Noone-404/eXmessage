package edu.kit.ss17.chatsys.team1.server.Storage;

import edu.kit.ss17.chatsys.team1.shared.Util.Account.Account;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import org.junit.Assert;

/**
 *
 */
public class StorageTest {

	@org.junit.Test
	public void saveGetAccount() throws Exception {
		JID              jid     = new JID("test@test.de/home");
		AccountInterface account = new Account(jid, "asdf");

		Storage storage = Storage.getInstance();
		storage.saveAccount(account);

		JID              jid2     = new JID("test@test.de/work");
		AccountInterface account2 = storage.getAccount(jid2);

		// Cleanup
		storage.removeAccount(account);

		Assert.assertTrue(account.getJid().getBaseJID().equals(account2.getJid().getBaseJID()) &&
		                  account.getPassword().equals(account2.getPassword()));
	}
}
