package edu.kit.ss17.chatsys.team1.client.GUI.View.ControlWrapper;

import edu.kit.ss17.chatsys.team1.client.Model.TextContentFragmentInterface;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;

/**
 * Wrapper for our text input control.
 */
public interface TextInputWrapperInterface<T extends Node> extends ControlWrapperInterface<T> {

	/**
	 * Get the range of selected input.
	 */
	IndexRange getSelection();

	/**
	 * Gets the property that resembles the content tree of the control
	 */
	Property<TextContentFragmentInterface> textContentProperty();

	/**
	 * Clears the content
	 */
	void clear();
}
