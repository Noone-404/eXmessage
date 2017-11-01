package edu.kit.ss17.chatsys.team1.client.Model;

import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.Account;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class RosterTest {

	@Test
	public void setGetContacts() throws Exception {
		Roster                       roster   = new Roster();
		Collection<ContactInterface> contacts = new ArrayList<>();

		JID jid = new JID("test@example.com/home");
		contacts.add(new Contact(jid, "test", false, roster));

		JID jid2 = new JID("test2@example.com/work");
		contacts.add(new Contact(jid2, "test2", false, roster));

		roster.setContacts(contacts);
		Assert.assertEquals(roster.getContacts(), contacts);
	}

	@Test
	public void setGetAccount() throws Exception {
		Roster  roster  = new Roster();
		JID     jid     = new JID("test@example.com/home");
		Account account = new Account(jid, "asdf");

		roster.setAccount(account);

		Assert.assertEquals(account, roster.getAccount());
	}

	@Test
	public void createGetDeleteContact() throws Exception {
		Roster roster = new Roster();

		JID              jid     = new JID("test@example.com/home");
		ContactInterface contact = roster.createContact(jid, "test", false);

		Assert.assertEquals(contact, roster.getContact(jid));

		roster.deleteContact(jid);
		Assert.assertEquals(null, roster.getContact(jid));
	}
}
