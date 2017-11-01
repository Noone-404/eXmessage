package edu.kit.ss17.chatsys.team1.shared.ProtocolError;

import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObserverInterface;

/**
 * Observer that is notified if a error occures inside a component of the {@Link ConnectionStack}.
 */
@FunctionalInterface
public interface ErrorManagerObserverInterface extends ObserverInterface {

	/**
	 * Invoked if an error occured.
	 *
	 * @param error the error object associated with the error.
	 */
	void errorReceived(ProtocolErrorInterface error);
}
