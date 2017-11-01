package edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers;

/**
 *
 */
public interface ObservableInterface<T extends ObserverInterface> {

	void registerObserver(T observer);

	void unregisterObserver(T observer);
}
