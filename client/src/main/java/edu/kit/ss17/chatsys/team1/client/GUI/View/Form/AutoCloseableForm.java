package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 */
public abstract class AutoCloseableForm implements AutoCloseableFormInterface {

	private Set<AutoCloseableFormObserverInterface> observers = new CopyOnWriteArraySet<>();

	@Override
	public void close() {
		performCloseAction();
		this.observers.forEach(AutoCloseableFormObserverInterface::closed);
	}

	@Override
	public void registerObserver(AutoCloseableFormObserverInterface observer) {
		this.observers.add(observer);
	}

	@Override
	public void unregisterObserver(AutoCloseableFormObserverInterface observer) {
		this.observers.remove(observer);
	}

	protected abstract void performCloseAction();
}
