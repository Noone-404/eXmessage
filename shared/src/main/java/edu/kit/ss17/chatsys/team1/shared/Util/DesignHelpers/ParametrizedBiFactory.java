package edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers;

/**
 *
 */
@FunctionalInterface
public interface ParametrizedBiFactory<T1, T2, T3> {

	T1 makeInstance(T2 parameter1, T3 parameter2);
}
