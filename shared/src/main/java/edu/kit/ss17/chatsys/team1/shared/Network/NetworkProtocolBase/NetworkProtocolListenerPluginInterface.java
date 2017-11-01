package edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase;

import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObservableInterface;

/**
 * Specifies how the network component interacts with a @code{NetworkProtocolPluginInterface} implementation.
 */
public interface NetworkProtocolListenerPluginInterface extends PluginInterface, ObservableInterface<NetworkConnectionOpenedInterface> {

	/**
	 * Opens a socket to listen for incoming connections.
	 */
	void startListening();

	/**
	 * Gets the current state (listening / idle).
	 */
	boolean isListening();

}
