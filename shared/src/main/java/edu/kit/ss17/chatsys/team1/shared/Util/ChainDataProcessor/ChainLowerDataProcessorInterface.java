package edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor;

/**
 *
 */
public interface ChainLowerDataProcessorInterface<T> {

	/**
	 * Is called from other ChainDataProcessor or ChainUpperDataProcessor instances to send data to this instance.
	 *
	 * @param data the data that is handed over to this instance
	 */
	void pushDataDown(T data);

	ChainUpperDataProcessorInterface<T> getUpper();
	void setUpper(ChainUpperDataProcessorInterface<T> newUpper);
}
