package edu.kit.ss17.chatsys.team1.client.GUI.Plugin;

import javafx.scene.Node;

import java.util.Collection;

/**
 * Plugin type, which can modify messages at both, the rich text input control and the conversation view.
 */
public interface GuiMessageContentPluginInterface extends GuiPluginInterface {

	/**
	 * Allows the plugin to add additional input controls to the gui.
	 */
	Collection<Node> getInputControls();
}
