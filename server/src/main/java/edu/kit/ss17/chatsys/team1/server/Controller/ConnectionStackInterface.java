package edu.kit.ss17.chatsys.team1.server.Controller;

import edu.kit.ss17.chatsys.team1.server.Router.RouterInterface;
import edu.kit.ss17.chatsys.team1.shared.ConnectionStack.ConnectionStackBaseInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StanzaInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;

/**
 * The server connection stack.
 */
public interface ConnectionStackInterface extends ConnectionStackBaseInterface {

	/**
	 * Get the full JID incl. resource part to which this stack belongs to.
	 */
	JID getJID();

	/**
	 * Tells the connection stack which router to use.
	 */
	void setRouter(RouterInterface router);

	/**
	 * Called by the router when a stanza has to be forwarded to the stacks user.
	 */
	void sendStanzaToUser(StanzaInterface stanza);

	/**
	 * Called by the StreamProcessor to forward a stanza to the router through this connection stack.
	 */
	void sendStanzaToOther(StanzaInterface stanza);
}
