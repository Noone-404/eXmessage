package edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers;

/**
 *
 */
@FunctionalInterface
public interface ParametrizedFactory<T1, T2> {

	T1 makeInstance(T2 parameter);
}
