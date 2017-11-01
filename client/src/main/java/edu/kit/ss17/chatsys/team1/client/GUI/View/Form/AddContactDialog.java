package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import com.sun.glass.ui.Size;
import com.sun.javafx.collections.ObservableListWrapper;
import edu.kit.ss17.chatsys.team1.client.GUI.AddContactFunction;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 *
 */
public class AddContactDialog extends FormBase implements AddContactDialogInterface {

	private static final Size   DEFAULT_SIZE = new Size(500, 350);
	private final        Object lock         = new Object();

	@FXML
	private Label            resultPlaceholder;
	@FXML
	private ListView<String> results;
	@FXML
	private TextField        searchBox;
	@FXML
	private Button           btAdd;
	@FXML
	private Button           btSearch;

	private Consumer<String>                        searchRequestCallback;
	private AddContactFunction                      addContactCallback;
	@SuppressWarnings("BooleanVariableAlwaysNegated")
	private boolean                                 expectingResults;
	private Collection<String>                      currentContacts;
	private Set<AutoCloseableFormObserverInterface> closeObservers;
	private AutoCloseableAlert                      addFailedAlert;

	public AddContactDialog() {
		this.closeObservers = new CopyOnWriteArraySet<>();
		this.searchBox.textProperty().addListener((observable, oldValue, newValue) -> this.btSearch.setDisable(newValue == null || newValue.isEmpty()));
		this.results.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> this.btAdd.setDisable(newValue == null || newValue.isEmpty()));
	}

	@Override
	public void show() {
		synchronized (this.lock) {
			this.expectingResults = false;
		}

		this.searchBox.clear();
		this.resultPlaceholder.setText(getLocaleResource().getString("contact_search_result_init"));
		this.results.getItems().clear();
		super.show();
	}

	@Override
	protected Size getDefaultSize() {
		return DEFAULT_SIZE;
	}

	@Override
	protected String getTitle() {
		return getLocaleResource().getString("contact_search_title");
	}

	@Override
	public void setSearchRequestCallback(Consumer<String> consumer) {
		this.searchRequestCallback = consumer;
	}

	@Override
	public void setContactAddCallback(AddContactFunction consumer) {
		this.addContactCallback = consumer;
	}

	@Override
	public void provideSearchResults(Collection<String> results) {
		synchronized (this.lock) {
			if (!this.expectingResults)
				return;
			this.expectingResults = false;
		}

		if (this.currentContacts != null && !this.currentContacts.isEmpty())
			this.currentContacts.forEach(jid -> {
				if (results.contains(jid))
					results.remove(jid);
			});

		if (results.isEmpty())
			this.resultPlaceholder.setText(getLocaleResource().getString("contact_search_result_empty"));

		this.results.setItems(new ObservableListWrapper<>(new ArrayList<>(results)));
	}

	@Override
	public void setCurrentContacts(Collection<String> currentContacts) {
		this.currentContacts = currentContacts;
	}

	@FXML
	private void btSearch(ActionEvent event) {
		this.resultPlaceholder.setText(getLocaleResource().getString("contact_search_result_running"));
		this.results.getItems().clear();
		synchronized (this.lock) {
			this.expectingResults = true;
		}

		this.searchRequestCallback.accept(this.searchBox.getText());
	}

	@FXML
	private void btCancel(ActionEvent event) {
		close();
	}

	@FXML
	private void btAdd(ActionEvent event) {
		String found = this.results.getSelectionModel().getSelectedItem();
		if (found == null || found.isEmpty())
			return;

		try {
			this.addContactCallback.accept(found);
		} catch (InvalidJIDException ignored) {
			if (this.addFailedAlert == null)
				this.addFailedAlert = new AutoCloseableAlert(
						AlertType.ERROR,
						getLocaleResource().getString("contact_add_failed_jid_title"),
						getLocaleResource().getString("contact_add_failed_jid_header"),
						getLocaleResource().getString("contact_add_failed_jid"),
						ButtonType.OK);

			this.addFailedAlert.showAndWait();
			return;
		}
		close();
	}

	@Override
	public void close() {
		this.getStage().close();
		if (this.addFailedAlert != null)
			this.addFailedAlert.close();
		this.closeObservers.forEach(AutoCloseableFormObserverInterface::closed);
	}

	@Override
	public void registerObserver(AutoCloseableFormObserverInterface observer) {
		this.closeObservers.add(observer);
	}

	@Override
	public void unregisterObserver(AutoCloseableFormObserverInterface observer) {
		this.closeObservers.remove(observer);
	}
}
