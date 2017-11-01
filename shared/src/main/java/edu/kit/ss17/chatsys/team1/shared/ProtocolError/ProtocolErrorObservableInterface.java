package edu.kit.ss17.chatsys.team1.shared.ProtocolError;

/**
 * Implemented by components where an error can occur.
 */
public interface ProtocolErrorObservableInterface {

	/**
	 * Registers a new observer at the component.
	 *
	 * @param observer the observer to register.
	 */
	void registerErrorObserver(ProtocolErrorObserverInterface observer);

	/**
	 * Unregisters a new observer at the component.
	 *
	 * @param observer the observer to unregister.
	 */
	void unregisterErrorObserver(ProtocolErrorObserverInterface observer);
}
