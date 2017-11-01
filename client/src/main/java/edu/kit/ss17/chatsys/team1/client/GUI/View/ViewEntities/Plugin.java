package edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities;

import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import javafx.beans.property.*;

/**
 *
 */
public class Plugin implements PluginInterface {

	private final ReadOnlyStringProperty  nameProperty;
	private final ReadOnlyStringProperty  versionProperty;
	private final ReadOnlyStringProperty  descriptionProperty;
	private final ReadOnlyBooleanProperty networkProperty;
	private final BooleanProperty         enabledProperty;

	public Plugin(PluginSetInterface psi, boolean isNetwork) {
		this.nameProperty = new ReadOnlyStringWrapper(psi.getName());
		this.versionProperty = new ReadOnlyStringWrapper(psi.getVersion());
		this.descriptionProperty = new ReadOnlyStringWrapper(psi.getDescription());
		this.networkProperty = new ReadOnlyBooleanWrapper(isNetwork);
		this.enabledProperty = psi.getEnabledProperty();
	}

	@Override
	public ReadOnlyStringProperty getNameProperty() {
		return this.nameProperty;
	}

	@Override
	public ReadOnlyStringProperty getVersionProperty() {
		return this.versionProperty;
	}

	@Override
	public ReadOnlyStringProperty getDescriptionProperty() {
		return this.descriptionProperty;
	}

	@Override
	public BooleanProperty getEnabledProperty() {
		return this.enabledProperty;
	}

	@Override
	public ReadOnlyBooleanProperty getIsNetworkProperty() {
		return this.networkProperty;
	}
}
