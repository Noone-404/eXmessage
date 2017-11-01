package edu.kit.ss17.chatsys.team1.server.Session;

import edu.kit.ss17.chatsys.team1.shared.Util.JID;

/**
 * Interface for Negotiators that negotiate authentication
 */
public interface AuthenticatorInterface extends NegotiatorInterface {

	/**
	 * returns the JID that has been authenticated
	 *
	 * @return authenticated JID
	 */
	JID getJid();
}
