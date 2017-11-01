package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import edu.kit.ss17.chatsys.team1.client.GUI.View.Form.ContactListCellItem.ContactState;
import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 *
 */
class ContactCell extends ListCell<ContactInterface> {

	private static final Logger              logger       = LogManager.getLogger(APP_NAME);
	private static       ContactListCellItem dummyElement = new ContactListCellItem(null, null, false, true);

	private ContactListCellItem        contactCellItem;
	private Consumer<ContactInterface> deleteContactConsumer;

	ContactCell(ResourceBundle localeBundle, Consumer<ContactInterface> deleteContactConsumer) {
		super();
		this.setPadding(new Insets(0.0f));
		this.deleteContactConsumer = deleteContactConsumer;
		initActions(localeBundle);
	}

	private static void editAction(ContactInterface item, AutoCloseableInputDialog changeAliasDialog) {
		logger.trace("Showing alias change dialog for contact " + System.identityHashCode(item));
		changeAliasDialog.getEditor().setText(item.getAlias());
		changeAliasDialog.showAndWait().ifPresent(item::setAlias);
	}

	private static Function<ContactInterface, EventHandler<ContextMenuEvent>> buildContextMenuRequestedAction(ResourceBundle localeBundle, ContextMenu contextMenu) {
		MenuItem       persistence      = new MenuItem(localeBundle.getString("contact_menu_accept"));
		List<MenuItem> persistenceItems = Arrays.asList(new SeparatorMenuItem(), persistence);
		return item -> event -> {
			if (item != null) {
				if (!item.isPersistent() && !contextMenu.getItems().containsAll(persistenceItems)) {
					contextMenu.getItems().addAll(persistenceItems);
				} else if (item.isPersistent() && contextMenu.getItems().containsAll(persistenceItems)) {
					contextMenu.getItems().removeAll(persistenceItems);
				}

				if (!item.isPersistent())
					persistence.setOnAction(e -> item.makePersistent());
			}
		};
	}

	private void initActions(ResourceBundle localeBundle) {
		ContextMenu contextMenu = new ContextMenu();

		AutoCloseableInputDialog changeAliasDialog = new AutoCloseableInputDialog(localeBundle.getString("contact_edit_title"),
		                                                                          localeBundle.getString("contact_edit_caption"),
		                                                                          localeBundle.getString("contact_edit_label"));

		MenuItem editItem = new MenuItem(localeBundle.getString("contact_menu_edit"));
		editItem.setOnAction(e -> editAction(this.getItem(), changeAliasDialog));

		MenuItem deleteItem = new MenuItem(localeBundle.getString("contact_menu_delete"));
		deleteItem.setOnAction(event -> deleteAction(this.getItem()));

		contextMenu.getItems().addAll(editItem, deleteItem);

		this.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
			if (isNowEmpty) {
				this.setContextMenu(null);
				changeAliasDialog.close();
			} else {
				this.setContextMenu(contextMenu);
			}
		});

		Function<ContactInterface, EventHandler<ContextMenuEvent>> contextMenuRequested = buildContextMenuRequestedAction(localeBundle, contextMenu);
		this.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, e -> contextMenuRequested.apply(this.getItem()).handle(e)); // must not use direct reference or qualifier
		this.listViewProperty().addListener((observable, oldValue, newValue) -> newValue.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			ContactInterface item;

			if (newValue == null || (item = newValue.getSelectionModel().getSelectedItem()) == null)
				return;

			//noinspection ObjectEquality
			if (this.getItem() != item)
				return;

			switch (event.getCode()) {
				case F2:
					editAction(item, changeAliasDialog);
					break;
				case CONTEXT_MENU:
					contextMenuRequested.apply(item).handle(null);
					Bounds absoluteBounds = this.localToScreen(this.getBoundsInLocal());
					this.getContextMenu().show(this,
					                           absoluteBounds.getMinX() + absoluteBounds.getWidth() / 2,
					                           absoluteBounds.getMinY() + absoluteBounds.getHeight() / 2);
					break;
				default:
			}
		}));
	}

	private void deleteAction(ContactInterface item) {
		this.deleteContactConsumer.accept(item);
	}

	@Override
	public void updateItem(ContactInterface item, boolean empty) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> updateItem(item, empty));
			return;
		}

		super.updateItem(item, empty);
		if (item == null) {
			setGraphic(dummyElement.getBox());
			return;
		}

		if (this.contactCellItem != null)
			this.contactCellItem.getSelectedProperty().unbind();

		this.contactCellItem = new ContactListCellItem(item.getJid().getFullJID(), item.getAlias(), item.isPersistent(),
		                                               ContactState.fromPresence(item.presenceProperty().getValue()));
		this.contactCellItem.getSelectedProperty().bind(selectedProperty());

		setGraphic(this.contactCellItem.getBox());
	}
}
