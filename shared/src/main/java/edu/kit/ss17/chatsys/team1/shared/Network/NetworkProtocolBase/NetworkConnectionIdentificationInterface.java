package edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase;

/**
 * Used by @code{NetworkProtocolListenerPluginInterface} implementations to communicate protocol specific identification information to a @code{NetworkProtocolPluginInterface}
 * implementation.
 */
public interface NetworkConnectionIdentificationInterface {

	/**
	 * @return the target address.
	 */
	String getAddress();

	/**
	 * @return the target port.
	 */
	int getPort();
}
