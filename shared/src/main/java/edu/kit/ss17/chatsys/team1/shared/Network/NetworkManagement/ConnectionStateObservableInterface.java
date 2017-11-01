package edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement;

/**
 * Interface for Classes that represent a network connection and can inform Observers when the connection is lost
 */
public interface ConnectionStateObservableInterface {

	/**
	 * register an observer to be notified when the connection is lost
	 * @param observer  Observer to be registered
	 */
	void registerConnectionStateObserver(ConnectionLostObserverInterface observer);

	/**
	 * unregister an observer to be notified when the connection is lost
	 * @param observer  Observer to be unregistered
	 */
	void unregisterConnectionStateObserver(ConnectionLostObserverInterface observer);

}
