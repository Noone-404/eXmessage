package edu.kit.ss17.chatsys.team1.shared.Network.NetworkDataProcessor;

import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;

/**
 * Interface that needs to be implemented by a network plugin (e.g. a TLS plugin) developer.
 */
public interface NetworkPluginInterface extends PluginInterface {

	byte[] dataFromUpperReceived(byte[] data);

	byte[] dataFromLowerReceived(byte[] data);
}
