package edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor;

import org.jetbrains.annotations.Nullable;

/**
 *
 */
public abstract class ChainDataProcessor<T1, T2> implements ChainDataProcessorInterface<T1, T2> {

	private ChainLowerDataProcessorInterface<T1> lower;
	private ChainUpperDataProcessorInterface<T2> upper;

	public static <T> void link(ChainLowerDataProcessorInterface<T> lower, ChainUpperDataProcessorInterface<T> upper) {
		lower.setUpper(upper);
		upper.setLower(lower);
	}

	public ChainLowerDataProcessorInterface<T1> getLower() {
		return this.lower;
	}

	public void setLower(ChainLowerDataProcessorInterface<T1> newLower) {
		this.lower = newLower;
	}

	public ChainUpperDataProcessorInterface<T2> getUpper() {
		return this.upper;
	}

	public void setUpper(ChainUpperDataProcessorInterface<T2> newUpper) {
		this.upper = newUpper;
	}

	/**
	 * Used by @code{this.upper} to send data to @code{this.lower}.
	 *
	 * @param data the data to send
	 */
	public void pushDataDown(T2 data) {
		T1 processedData = processDataFromUpper(data);
		if (processedData != null) {
			this.getLower().pushDataDown(processedData);
		}
	}

	/**
	 * Used by @code{this.lower} to send data to @code{this.upper}.
	 *
	 * @param data the data to send
	 */
	public void pushDataUp(T1 data) {
		T2 processedData = processDataFromLower(data);
		if (processedData != null) {
			this.getUpper().pushDataUp(processedData);
		}
	}

	/**
	 * Processes data from {@code this.upper}. The return value is send to {@code this.lower}.
	 * <p>
	 * This function may choose to change {@code this.lower} as to change the destination of the data.
	 *
	 * @param data The data to process
	 *
	 * @return The processed data
	 */
	@Nullable
	protected abstract T1 processDataFromUpper(T2 data);

	/**
	 * Processes data from {@code this.upper}. The return value is send to {@code this.upper}.
	 * <p>
	 * This function may choose to change {@code this.upper} as to change the destination of the data.
	 *
	 * @param data The data to process
	 *
	 * @return The processed data
	 */
	@Nullable
	protected abstract T2 processDataFromLower(T1 data);
}
