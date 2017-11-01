package edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;

/**
 *
 */
public interface PluginInterface {

	ReadOnlyStringProperty getNameProperty();
	ReadOnlyStringProperty getVersionProperty();
	ReadOnlyStringProperty getDescriptionProperty();
	ReadOnlyBooleanProperty getIsNetworkProperty();
	BooleanProperty getEnabledProperty();
}
