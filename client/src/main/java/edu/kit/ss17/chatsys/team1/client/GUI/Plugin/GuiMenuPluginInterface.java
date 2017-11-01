package edu.kit.ss17.chatsys.team1.client.GUI.Plugin;


import javafx.scene.control.MenuItem;

import java.util.Collection;

/**
 * Plugin type, which can modify messages at both, the rich text input control and the conversation view.
 */
public interface GuiMenuPluginInterface extends GuiPluginInterface {

	Collection<MenuItem> getMenuItems();

}
