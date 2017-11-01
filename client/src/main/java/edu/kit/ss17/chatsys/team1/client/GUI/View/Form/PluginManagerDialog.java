package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import com.sun.glass.ui.Size;
import com.sun.javafx.collections.ObservableListWrapper;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.PluginInterface;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class PluginManagerDialog extends FormBase implements PluginManagerDialogInterface {

	private static final Size DEFAULT_SIZE = new Size(600, 300);

	@FXML
	private TableView<PluginInterface> pluginTable;

	@FXML
	private TableColumn<PluginInterface, PluginInterface> enabledColumn;
	@FXML
	private TableColumn<PluginInterface, String>          nameColumn;
	@FXML
	private TableColumn<PluginInterface, String>          descColumn;
	@FXML
	private TableColumn<PluginInterface, String>          versionColumn;

	private HashMap<String, Boolean> changedEntries;

	public PluginManagerDialog() {
		this.changedEntries = new HashMap<>();
		PluginSetCell.setChangeOccurredCallback((pluginInterface, aBoolean) -> this.changedEntries.put(
				pluginInterface.getNameProperty().get(),
				this.changedEntries.getOrDefault(pluginInterface.getNameProperty().get(), false) ^ aBoolean));

		getStage().setOnCloseRequest(event -> beforeClose());

		this.nameColumn.setCellValueFactory(p -> p.getValue().getNameProperty());
		this.descColumn.setCellValueFactory(p -> p.getValue().getDescriptionProperty());
		this.versionColumn.setCellValueFactory(p -> p.getValue().getVersionProperty());

		this.enabledColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue()));
		this.enabledColumn.setCellFactory(p -> new PluginSetCell());
	}

	@Override
	protected Size getDefaultSize() {
		return DEFAULT_SIZE;
	}

	@Override
	protected String getTitle() {
		return getLocaleResource().getString("pm_title");
	}

	private void beforeClose() {
		if (this.changedEntries.values().stream().anyMatch(changed -> changed)) {
			Alert alert = new Alert(AlertType.INFORMATION, getLocaleResource().getString("pm_alert_changed_text"), ButtonType.OK);
			((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(getIcon());
			alert.setTitle(getLocaleResource().getString("pm_alert_changed_title"));
			alert.setHeaderText("");
			alert.showAndWait();
		}
		// TODO: check if it is necessary to reset changed entries in list view
		this.changedEntries.clear();
	}

	@FXML
	@SuppressWarnings("unused")
	private void btClose(ActionEvent event) {
		beforeClose();
		getStage().close();
	}

	@Override
	public void setPlugins(List<PluginInterface> plugins) {
		this.pluginTable.setItems(new ObservableListWrapper<>(plugins));
	}
}
