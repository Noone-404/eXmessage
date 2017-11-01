package edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement;

import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginableInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.Factory;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObservableInterface;

/**
 * Specifies the methods that need to be implemented by a factory that produces instances of a given @code{NetworkStackInterface} implementation.
 */
public interface NetworkStackFactoryInterface extends Factory<NetworkStackInterface>, PluginableInterface, ObservableInterface<NetworkStackFactoryObserverInterface> {

	/**
	 * Opens a socket to listen for incoming connections.
	 */
	void startListening();
}
