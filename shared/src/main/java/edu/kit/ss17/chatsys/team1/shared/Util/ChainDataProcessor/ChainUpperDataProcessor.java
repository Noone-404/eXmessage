package edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor;

/**
 *
 */
public abstract class ChainUpperDataProcessor<T> implements ChainUpperDataProcessorInterface<T> {

	private ChainLowerDataProcessorInterface<T> lower;

	public ChainLowerDataProcessorInterface<T> getLower() {
		return this.lower;
	}

	public void setLower(ChainLowerDataProcessorInterface<T> newLower) {
		this.lower = newLower;
	}
}
