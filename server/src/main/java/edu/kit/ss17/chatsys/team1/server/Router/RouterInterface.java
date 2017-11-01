package edu.kit.ss17.chatsys.team1.server.Router;

import edu.kit.ss17.chatsys.team1.server.Controller.ConnectionStackInterface;
import edu.kit.ss17.chatsys.team1.server.Storage.Storage;
import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StanzaInterface;

/**
 * Interface for the @code{Router} component which is responsible to route messages between accounts.
 */
public interface RouterInterface {

	/**
	 * Register a ConnectionStack. Usually after successful authentication.
	 */
	void registerConnectionStack(ConnectionStackInterface stack);

	/**
	 * Unregister ConnectionStack. Usually after logout.
	 */
	void unregisterConnectionStack(ConnectionStackInterface stack);

	/**
	 * Send a stanza to its receiver-JID.
	 */
	void sendStanza(StanzaInterface stanza);

	/**
	 * Processes a stanza that is destined to the server only.
	 */
	void processStanza(StanzaInterface stanza);

	/**
	 * Make Storage accessible to router.
	 */
	void setStorage(StorageInterface storage);
}
