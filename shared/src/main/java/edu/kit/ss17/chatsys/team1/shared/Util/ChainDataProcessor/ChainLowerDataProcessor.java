package edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor;

/**
 *
 */
public abstract class ChainLowerDataProcessor<T> implements ChainLowerDataProcessorInterface<T> {

	private ChainUpperDataProcessorInterface<T> upper;

	public ChainUpperDataProcessorInterface<T> getUpper() {
		return this.upper;
	}

	public void setUpper(ChainUpperDataProcessorInterface<T> newUpper) {
		this.upper = newUpper;
	}
}
