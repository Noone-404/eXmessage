package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.PluginInterface;

import java.util.List;

/**
 *
 */
public interface PluginManagerDialogInterface extends FormBaseInterface {

	void setPlugins(List<PluginInterface> plugins);
}
