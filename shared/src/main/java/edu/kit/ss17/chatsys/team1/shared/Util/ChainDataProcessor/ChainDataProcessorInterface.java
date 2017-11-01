package edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor;

/**
 * Subsumes @code{ChainLowerDataProcessorInterface} and @code{ChainUpperDataProcessorInterface}.
 */
public interface ChainDataProcessorInterface<T1, T2> extends ChainLowerDataProcessorInterface<T2>, ChainUpperDataProcessorInterface<T1> {

}
