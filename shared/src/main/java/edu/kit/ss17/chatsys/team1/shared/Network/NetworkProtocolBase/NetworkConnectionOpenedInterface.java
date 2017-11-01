package edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase;

import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObserverInterface;

/**
 * Functional interface that allows the @code{NetworkStackFactory} to inform the @code{Controller} that a new network connection to this sever has been requested.
 */
@FunctionalInterface
public interface NetworkConnectionOpenedInterface extends ObserverInterface {

	/**
	 * Is invoked if a {@Link NetworkProtocolListenerPluginInterface} implementation detects a new connection.
	 *
	 * @param networkConnectionIdentification the {@Link NetworkConnectionIdentificationInterface} that corresponds to the new connection.
	 */
	void connectionOpened(NetworkConnectionIdentificationInterface networkConnectionIdentification);
}
