package edu.kit.ss17.chatsys.team1.plugins.TCPConnectionPlugin;

import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkConnectionIdentificationInterface;

import java.net.Socket;

/**
 * Used by the default {@Link NetworkProtocolListenerPluginInterface} implementation to transport a TCP-specific {@Link Socket} object.
 */
interface SocketConnectionIdentificationInterface extends NetworkConnectionIdentificationInterface {

	/**
	 * @return a TCP-specific {@Link Socket} object
	 */
	Socket getSocket();
}
