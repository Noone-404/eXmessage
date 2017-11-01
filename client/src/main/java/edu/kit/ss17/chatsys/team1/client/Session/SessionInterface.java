package edu.kit.ss17.chatsys.team1.client.Session;

import edu.kit.ss17.chatsys.team1.shared.Session.SessionBaseInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountInterface;

/**
 * Maintains the current session.
 */
public interface SessionInterface extends SessionBaseInterface {

	/**
	 * Initializes the session with the given account information.
	 *
	 * @param account an @code{AccountConfiguration} instance (contains JID, server, password etc.)
	 */
	void initSession(AccountInterface account);
}
