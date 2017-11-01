package edu.kit.ss17.chatsys.team1.server.Session;

import edu.kit.ss17.chatsys.team1.shared.Session.SessionBaseInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;

/**
 * Session for one connected jid/resource.
 */
public interface SessionInterface extends SessionBaseInterface {

	/**
	 * Returns the JID for this Session
	 *
	 * @return JID for this Session
	 */
	JID getJid();
}
