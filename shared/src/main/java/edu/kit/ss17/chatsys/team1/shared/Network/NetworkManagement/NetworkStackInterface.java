package edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement;

import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkConnectionIdentificationInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginableInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainDataProcessorInterface;

import java.io.IOException;

/**
 * Represents an incoming connection (server) or outgoing connection (client).
 */
public interface NetworkStackInterface extends PluginableInterface, ChainDataProcessorInterface<byte[], byte[]>, ConnectionStateObservableInterface {

	/**
	 * Sets the NetworkConnectionInformationInterface used by connect().
	 *
	 * @param connectionInfo The NetworkConnectionInformationInterface to identify the new connection.
	 */
	void setConnectionInfo(NetworkConnectionIdentificationInterface connectionInfo);

	/**
	 * Opens a new connection using the information specified in the NetworkConnectionInformation-Object. Currently only used client side.
	 */
	void connect() throws IOException;

	/**
	 * Closes the connection
	 */
	void disconnect();

	/**
	 * Used server side to tell the NetworkStack which ProtocolPlugin to use.
	 *
	 * @param protocolName the name of the ProtocolPlugins Pluginset name.
	 */
	void setProtocol(String protocolName);

}
