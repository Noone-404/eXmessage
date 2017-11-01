package edu.kit.ss17.chatsys.team1.client.GUI.Plugin;

import edu.kit.ss17.chatsys.team1.client.GUI.Plugin.PluginAccessor.GuiPluginAccessorInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;

/**
 * Plugin type, which can modify messages at both, the rich text input control and the conversation view.
 */
public interface GuiPluginInterface extends PluginInterface {

	/**
	 * Sets the accessor which provides gui data necessary for plugins.
	 */
	void setGuiAccessor(GuiPluginAccessorInterface guiAccessor);

}
