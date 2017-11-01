package edu.kit.ss17.chatsys.team1.client.GUI.View.ControlWrapper;

import javafx.scene.Node;

/**
 *
 */
public interface ControlWrapperInterface<T extends Node> {

	/**
	 * Gets the control for displaying in view
	 */
	T getControl();
}
