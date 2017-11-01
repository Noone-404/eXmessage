package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.PluginInterface;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;

import java.util.function.BiConsumer;

/**
 *
 */
class PluginSetCell extends TableCell<PluginInterface, PluginInterface> {

	private static BiConsumer<PluginInterface, Boolean> callback;
	private        CheckBox                             cb;
	private        PluginInterface                      vpi;

	static void setChangeOccurredCallback(BiConsumer<PluginInterface, Boolean> callback) {
		PluginSetCell.callback = callback;
	}

	@Override
	public void updateItem(PluginInterface item, boolean empty) {
		super.updateItem(item, empty);

		if (item == null) {
			setGraphic(null);
			return;
		}

		if (this.vpi == null)
			this.vpi = item;

		if (this.cb == null || !item.equals(this.vpi)) {
			this.cb = new CheckBox();
			this.cb.setVisible(!this.vpi.getIsNetworkProperty().getValue());
			this.cb.setSelected(this.vpi.getEnabledProperty().getValue());
			this.cb.selectedProperty().bindBidirectional(this.vpi.getEnabledProperty());
			this.cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if (callback != null)
					callback.accept(this.vpi, newValue != oldValue);
			});
		}

		setGraphic(this.cb);
	}
}
