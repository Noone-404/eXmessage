package edu.kit.ss17.chatsys.team1.shared.Session;

import edu.kit.ss17.chatsys.team1.shared.ConnectionStack.ConnectionStackBaseInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginableInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObservableInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValueInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainUpperDataProcessorInterface;

/**
 * The base interface for a @code{Session}, used on client and server. The session is a chain upper data processor. Incoming data will be used to perform negotiation, and will be
 * ignored otherwise.
 */
public interface SessionBaseInterface extends PluginableInterface, ProtocolErrorObservableInterface, ChainUpperDataProcessorInterface<DataElementInterface> {

	/**
	 * Indicates whether negotiation is finished or not.
	 */
	boolean isNegotiationFinished();

	/**
	 * Set to true if we want to disconnect and just need to wait for the opponents stream closing tag.
	 */
	void setDisconnecting(boolean disconnecting);

	/**
	 * Returns true if we're currently waiting for the opponents stream closing tag.
	 */
	boolean isDisconnecting();

	/**
	 * Terminates the current session.
	 */
	void terminateSession();

	/**
	 * Sets the current users presence.
	 *
	 * @param newValue the new presence value
	 */
	void setPresence(PresenceValueInterface newValue);

	/**
	 * Gets the current users presence value.
	 *
	 * @return the current users presence value
	 */
	PresenceValueInterface getPresence();

	/**
	 * Tells the session its ConnectionStack context.
	 *
	 * @param connectionStack the ConnectionStack.
	 */
	void setConnectionStack(ConnectionStackBaseInterface connectionStack);
}
