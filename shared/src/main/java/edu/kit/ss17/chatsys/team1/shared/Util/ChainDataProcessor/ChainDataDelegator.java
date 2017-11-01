package edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor;

import org.jetbrains.annotations.Contract;

/**
 *
 */
public class ChainDataDelegator<T> extends ChainDataProcessor<T, T> {

	@Override
	@Contract(pure = true)
	protected final T processDataFromUpper(T data) {
		return data;
	}

	@Override
	@Contract(pure = true)
	protected final T processDataFromLower(T data) {
		return data;
	}

}
