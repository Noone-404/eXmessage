package edu.kit.ss17.chatsys.team1.shared.PluginManager;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObservableInterface;
import javafx.beans.property.BooleanProperty;

/**
 * Details within component plugin interface extensions. Type recognition happens by reflection within the components.
 */
public interface PluginInterface extends Cloneable, ProtocolErrorObservableInterface {

	BooleanProperty getEnabledProperty();

	int getWeight();

	PluginSetInterface getPluginSet();

	void setPluginSet(PluginSetInterface pluginSet);
}
