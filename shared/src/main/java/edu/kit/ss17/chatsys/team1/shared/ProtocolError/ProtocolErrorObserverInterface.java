package edu.kit.ss17.chatsys.team1.shared.ProtocolError;

import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObserverInterface;

/**
 * Functional interface that allows {@Link ProtocolErrorObservableInterface} implementations to tell the {@Link ErrorManager} that an error has occured.
 */
@FunctionalInterface
public interface ProtocolErrorObserverInterface extends ObserverInterface {

	/**
	 * Invoked if an error has occured.
	 *
	 * @param error a {@Link ProtocolErrorInterface} that describes the error.
	 */
	void onProtocolError(ProtocolErrorInterface error);
}
