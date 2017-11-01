package edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor;

/**
 *
 */
public interface ChainUpperDataProcessorInterface<T> {

	/**
	 * Is called from other ChainDataProcessor or ChainLowerDataProcessor instances to send data to this instance.
	 *
	 * @param data the data that is handed over to this instance
	 */
	void pushDataUp(T data);

	ChainLowerDataProcessorInterface<T> getLower();
	void setLower(ChainLowerDataProcessorInterface<T> newLower);
}
