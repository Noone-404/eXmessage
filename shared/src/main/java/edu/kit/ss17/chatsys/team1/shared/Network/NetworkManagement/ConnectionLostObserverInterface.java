package edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement;

/**
 * Interface for Observers that observe the state of a network connection
 */
public interface ConnectionLostObserverInterface {

	/**
	 * Method to be invoked when the Connection is lost
	 */
	void onConnectionLost(ConnectionLostReason reason);

	enum ConnectionLostReason {
		SOCKET_CLOSED, CONNECTION_RESET, EOF, ERROR;
	}
}
