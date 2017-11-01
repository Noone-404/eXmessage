package edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase;

import edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement.ConnectionStateObservableInterface;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkProtocolPluginInterface.NetworkProtocolPluginDataReceivedInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObservableInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObserverInterface;

import java.io.IOException;

/**
 * Specifies how the network component interacts with a @code{NetworkProtocolPluginInterface} implementation.
 */
public interface NetworkProtocolPluginInterface extends PluginInterface, ObservableInterface<NetworkProtocolPluginDataReceivedInterface>, ConnectionStateObservableInterface {

	/**
	 * Called when the listening plugin got a new connection and the newly created corresponding connection stack's protocol plugin should be bound to that connection.
	 */
	void connect(NetworkConnectionIdentificationInterface connectionInfo) throws IOException;

	/**
	 * Disconnect a possible connection this plugin is bound to.
	 */
	void disconnect();

	/**
	 * Sends bytes.
	 */
	void sendData(byte[] data);


	/**
	 * Functional interface that allows a {@Link NetworkProtocolPluginInterface} implementation to inform its {@Link NetworkStack} of new incoming data.
	 */
	interface NetworkProtocolPluginDataReceivedInterface extends ObserverInterface {

		/**
		 * Is invoked if the {@Link NetworkProtocolPluginInterface} implementation receives new data over its connection.
		 *
		 * @param data the received data.
		 */
		void dataReceived(byte[] data);
	}
}
