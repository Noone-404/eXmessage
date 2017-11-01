package edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement;

import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObserverInterface;

/**
 * Observes a NetworkStackFactory.
 */
public interface NetworkStackFactoryObserverInterface extends ObserverInterface {

	/**
	 * Called by the Network Factory once a new incoming connection has been established.
	 */
	void receivedNewConnection(NetworkStackInterface stack);
}
