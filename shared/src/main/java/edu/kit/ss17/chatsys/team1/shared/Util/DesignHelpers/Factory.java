package edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers;

/**
 *
 */
@FunctionalInterface
public interface Factory<T> {

	T makeInstance();
}
