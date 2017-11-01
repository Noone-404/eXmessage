package edu.kit.ss17.chatsys.team1.plugins.TCPConnectionPlugin;

import org.jetbrains.annotations.Contract;

/**
 *
 */
final class Box<T> { // Boxing needed to update final value

	private T element;

	Box(T element) {
		this.element = element;
	}

	@Contract(pure = true)
	public T getElement() {
		return this.element;
	}

	void setElement(T element) {
		this.element = element;
	}
}
