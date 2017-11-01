package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import com.sun.glass.ui.Size;
import com.sun.javafx.collections.ObservableListWrapper;
import edu.kit.ss17.chatsys.team1.client.GUI.ContentFragments.RenderableContentInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ControlWrapper.TextInputWrapperInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.ConnectionErrorReason;
import edu.kit.ss17.chatsys.team1.client.Model.ContentInterface;
import edu.kit.ss17.chatsys.team1.client.Model.TextContentFragmentInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValue;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValueInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 *
 */
public class MainForm extends FormBase implements MainFormInterface {

	private static final Size   DEFAULT_SIZE      = new Size(800, 600);
	private static final String MENU_PRESERVE_KEY = "preserve";
	private static final Logger logger            = LogManager.getLogger(APP_NAME);

	private static TextInputWrapperInterface<Node> textInputWrapper;

	private final Object autoCloseLock = new Object();

	@FXML
	private Label                            lblAlias;
	@FXML
	private Label                            lblOthersJid;
	@FXML
	private Label                            lblOwnJid;
	@FXML
	private Label                            lblOthersState;
	@FXML
	private Menu                             menuLanguage;
	@FXML
	private MenuItem                         menuAddAccountItem;
	@FXML
	private Menu                             menuPlugins;
	@FXML
	private Menu                             menuConnection;
	@FXML
	private ListView<ContactInterface>       contactListBox;
	@FXML
	private SplitPane                        mainContainer;
	@FXML
	private ComboBox<PresenceValueInterface> ownStatus;
	@FXML
	private VBox                             chatArea;
	@FXML
	private HBox                             othersInformation;
	@FXML
	private Pane                             enterChatMessagePane;
	@FXML
	private Pane                             conversationPane;
	@FXML
	private ScrollPane                       conversationScrollPane;
	@FXML
	private SplitPane                        conversationSplitPane;
	@FXML
	private Button                           btSend;

	private          StringProperty                                                                            selectedLanguage;
	private          ReadOnlyStringProperty                                                                    lastError;
	private          PluginManagerDialog                                                                       pluginManagerDialog;
	private          AccountConfigurationDialogInterface                                                       accountConfigurationDialog;
	private          AddContactDialogInterface                                                                 addContactDialog;
	private          ObservableList<? extends AccountConfigurationInterface>                                   accounts;
	private          Consumer<AccountConfigurationInterface>                                                   connectConsumer;
	private          Consumer<AccountConfigurationInterface>                                                   disconnectConsumer;
	private          Consumer<AccountConfigurationInterface>                                                   accountDeletedConsumer;
	private          Property<AccountConfigurationInterface>                                                   currentAccountConfigurationProperty;
	private          Consumer<PresenceValueInterface>                                                          presenceChangeCallback;
	private          ObservableList<ContactInterface>                                                          currentContacts;
	private          BiConsumer<ContentInterface, ContactInterface>                                            messageSendRequestedCallback;
	private          BiFunction<AccountConfigurationInterface, ContactInterface, Collection<MessageInterface>> messageFetcher;
	private          String                                                                                    currentLocaleTag;
	private          HashMap<ContactInterface, TextContentFragmentInterface>                                   typedText;
	private volatile Collection<AutoCloseableFormInterface>                                                    autoCloseables;
	private          Consumer<ContactInterface>                                                                contactDeletedConsumer;
	private          Consumer<Void>                                                                            successfullDisconnectedCallback;
	private          Consumer<RosterInterface>                                                                 rosterChangedConsumer; // internal

	protected MainForm() {
		initialize();
	}

	public MainForm(Stage primaryStage) {
		this.setStage(primaryStage);
		initialize();
	}

	public static void setTextInputWrapper(TextInputWrapperInterface<Node> wrapper) {
		textInputWrapper = wrapper;
	}

	private static int contactSorter(ContactInterface contact1, ContactInterface contact2) {
		if (contact1.isPersistent() && !contact2.isPersistent())
			return -1;

		if (contact2.isPersistent() && !contact1.isPersistent())
			return 1;

		return contact1.getJid().getBaseJID().compareToIgnoreCase(contact2.getJid().getBaseJID());
	}

	private void initialize() {
		if (textInputWrapper == null)
			throw new IllegalStateException("Text wrapper not set");

		this.selectedLanguage = new SimpleStringProperty();
		this.currentContacts = new ObservableListWrapper<>(new ArrayList<>(),
		                                                   param -> new Observable[] {
				                                                   param.aliasProperty(),
				                                                   param.presenceProperty(),
				                                                   param.persistenceProperty()
		                                                   });
		this.currentAccountConfigurationProperty = new SimpleObjectProperty<>();
		this.typedText = new HashMap<>();
		this.autoCloseables = new HashSet<>();
		this.othersInformation.prefHeightProperty().bind(this.lblOwnJid.heightProperty());
		this.lblAlias.managedProperty().bind(this.lblAlias.visibleProperty());
		this.lblAlias.textProperty().addListener((observable, oldValue, newValue) -> {
			boolean isSet = newValue != null && !newValue.isEmpty();
			this.lblAlias.setVisible(isSet);
			this.lblOthersJid.setTextFill(isSet ? Color.GRAY : Color.BLACK);
		});
		this.contactListBox.setCellFactory(list -> new ContactCell(getLocaleResource(), contact -> this.contactDeletedConsumer.accept(contact))); // must not use direct reference
		this.contactListBox.setItems(this.currentContacts);
		this.contactListBox.getSelectionModel().selectedItemProperty().addListener(this::selectedContactChanged);
		this.currentAccountConfigurationProperty.addListener((observable, oldValue, newValue) -> {
			this.lblOwnJid.setText(newValue != null ? newValue.getJid() : getLocaleResource().getString("default_own_jid"));
			updateAccountConfigurationMenu(null);
		});
		this.menuConnection.getItems().forEach(item -> item.getProperties().put(MENU_PRESERVE_KEY, true));
		this.ownStatus.getItems().addAll(PresenceValue.values());
		Callback<ListView<PresenceValueInterface>, ListCell<PresenceValueInterface>> factory;

		this.ownStatus.setCellFactory(factory = listView -> new ComboBoxListCell<PresenceValueInterface>() {
			@Override
			public void updateItem(PresenceValueInterface item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null) {
					setText(item.getHumanReadableName(getLocaleResource()));
				} else {
					setText(null);
				}
			}
		});
		this.ownStatus.setButtonCell(factory.call(null));
		this.ownStatus.setValue(this.ownStatus.getItems().get(0));

		AutoCloseableAlert.setDefaultIcon(getIcon());
		AutoCloseableInputDialog.setDefaultIcon(getIcon());
		Node textInputControl = textInputWrapper.getControl();
		AnchorPane.setTopAnchor(textInputControl, 0.0);
		AnchorPane.setRightAnchor(textInputControl, 0.0);
		AnchorPane.setBottomAnchor(textInputControl, 0.0);
		AnchorPane.setLeftAnchor(textInputControl, 0.0);
		this.enterChatMessagePane.getChildren().add(textInputControl);
		textInputControl.disableProperty().bind(this.chatArea.disableProperty());
		textInputControl.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER)
				if (event.isShiftDown()) {
					try { // Sadly there is no built-in way to do this
						Field field = event.getClass().getDeclaredField("shiftDown");
						field.setAccessible(true);
						field.set(event, false);
					} catch (NoSuchFieldException | IllegalAccessException e) {
						logger.warn("Failed to modify event for propagation: " + e.getMessage());
					}
				} else if (!MainForm.this.btSend.isDisabled()) {
					event.consume();
					MainForm.this.btSend.fire();
				}
		});

		textInputWrapper.textContentProperty().addListener((observable, oldValue, newValue) -> {
			this.btSend.setDisable(newValue == null || newValue.getPlaintextRepresentation() == null || newValue.getPlaintextRepresentation().trim().isEmpty());
			this.typedText.put(getSelectedContact(), newValue);
		});

		this.conversationPane.heightProperty().addListener(il -> this.conversationScrollPane.setVvalue(this.conversationScrollPane.getVmax()));
	}

	private void selectedContactChanged(Observable observable, ContactInterface oldValue, ContactInterface newValue) {
		if (newValue == null) {
			this.lblOthersJid.setText(getLocaleResource().getString("default_others_jid"));
			this.chatArea.setDisable(true);
			this.lblAlias.textProperty().unbind();
			this.lblAlias.setText("");
			this.lblOthersState.textProperty().unbind();
			this.lblOthersState.setText(getLocaleResource().getString("default_others_state"));
			this.conversationPane.getChildren().clear();
			textInputWrapper.textContentProperty().setValue(null);
		} else {
			this.lblOthersJid.setText(newValue.getJid().getBaseJID());
			this.chatArea.setDisable(false);
			StringProperty intermediateProp = new SimpleStringProperty(newValue.presenceProperty().getValue().getHumanReadableName(getLocaleResource()));
			newValue.presenceProperty().addListener((observable1, oldValue1, newValue1)
					                                        -> Platform.runLater(() -> intermediateProp.setValue(newValue1.getHumanReadableName(getLocaleResource()))));
			this.lblOthersState.textProperty().bind(intermediateProp);
			this.lblAlias.textProperty().bind(newValue.aliasProperty());
			this.conversationPane.getChildren().clear();
			this.conversationPane.getChildren().addAll(this.messageFetcher
					                                           .apply(this.currentAccountConfigurationProperty.getValue(), newValue)
					                                           .stream()
					                                           .filter(msg -> msg instanceof RenderableContentInterface)
					                                           .sorted(Comparator.comparing(
							                                           msg -> (msg.getSender().equals(this.currentAccountConfigurationProperty.getValue().getJid())
							                                                   ? msg.getClientSendDate()
							                                                   : msg.getServerReceiveDate())))
					                                           .flatMap(msg -> Stream.of(((RenderableContentInterface) msg).getContent()).map(
							                                           r -> renderMessage(r,
							                                                              msg.getSender().equals(this.currentAccountConfigurationProperty.getValue().getJid()),
							                                                              msg.getClientSendDate())))
					                                           .collect(Collectors.toList()));
			textInputWrapper.textContentProperty().setValue(this.typedText.get(newValue));
		}
	}

	private void addCloseable(AutoCloseableFormInterface autoCloseable) {
		addCloseable(autoCloseable, true);
	}

	private void addCloseable(AutoCloseableFormInterface autoCloseable, boolean singleClosable) {
		synchronized (this.autoCloseLock) {
			if (singleClosable)
				autoCloseable.registerObserver(() -> {
					synchronized (this.autoCloseLock) {
						this.autoCloseables.remove(autoCloseable);
					}
				});
			this.autoCloseables.add(autoCloseable);
		}
	}

	@NotNull
	private Collection<AutoCloseableFormInterface> getCloseables() {
		synchronized (this.autoCloseLock) {
			return new ArrayList<>(this.autoCloseables);
		}
	}

	private void setLocale(String id) {
		this.selectedLanguage.setValue(id);

		new AutoCloseableAlert(AlertType.INFORMATION,
		                       getLocaleResource().getString("language_alert_title"),
		                       getLocaleResource().getString("language_alert_header"),
		                       getLocaleResource().getString("language_alert_text"),
		                       ButtonType.OK)
				.showAndWait();
	}

	private void updateAccountConfigurationMenu(@Nullable Change<? extends AccountConfigurationInterface> ignored) {
		ObservableList<MenuItem> menuItems = this.menuConnection.getItems();
		menuItems.removeIf(menuItem -> !((boolean) menuItem.getProperties().getOrDefault(MENU_PRESERVE_KEY, false)));

		if (!this.accounts.isEmpty())
			menuItems.addAll(
					menuItems.indexOf(menuItems.stream().filter(item -> item instanceof SeparatorMenuItem).reduce((first, second) -> second).orElse(menuItems.get(0))) + 1,
					Stream.concat(
							this.accounts.stream()
							             .sorted((o1, o2) -> o1.getJid().compareToIgnoreCase(o2.getJid()))
							             .map(this::createAccountMenuItem),
							Stream.of(new SeparatorMenuItem()))
					      .collect(Collectors.toList()));
	}

	private MenuItem createAccountMenuItem(AccountConfigurationInterface account) {
		boolean currentAccount = account.equals(this.currentAccountConfigurationProperty.getValue());

		boolean unique = MainForm.this.accounts.stream()
		                                       .map(AccountConfigurationInterface::getJid)
		                                       .filter(account.getJid()::equals)
		                                       .count() == 1;

		Menu     menu         = new Menu(unique ? account.getJid() : account.getJid() + " (" + account.getAddress() + ':' + account.getPort() + ')');
		MenuItem miEdit       = new MenuItem(getLocaleResource().getString("menu_acc_edit"));
		MenuItem miDisConnect = new MenuItem(getLocaleResource().getString(currentAccount ? "menu_acc_disconnect" : "menu_acc_connect"));
		MenuItem miDelete     = new MenuItem(getLocaleResource().getString("menu_acc_remove"));

		miEdit.setOnAction(event -> this.accountConfigurationDialog.showForAccount(account));
		miDisConnect.setOnAction(e -> disConnectRequested(account, currentAccount));
		miDelete.setOnAction(e -> deleteRequested(account));

		if (!currentAccount && account.getProtocol().trim().isEmpty())
			miDisConnect.setDisable(true);

		menu.getItems().addAll(miDisConnect, new SeparatorMenuItem(), miEdit, miDelete);

		return menu;
	}

	private void disConnectRequested(AccountConfigurationInterface account, boolean currentAccount) {
		if (currentAccount) { // request to disconnect from current account
			if (!confirmDisconnect(false))
				return;
			this.ownStatus.setValue(this.ownStatus.getItems().get(0));
			this.showWaitMessage(false);
			this.disconnectConsumer.accept(account);
		} else { // request to connect to another account
			if (this.currentAccountConfigurationProperty.getValue() != null) { // we are currently connected - disconnect first
				if (!confirmDisconnect(true))
					return;
				this.ownStatus.setValue(this.ownStatus.getItems().get(0));
				this.showWaitMessage(false);
				this.successfullDisconnectedCallback = (v) -> {
					this.successfullDisconnectedCallback = null;
					this.showWaitMessage(true);
					this.currentAccountConfigurationProperty.setValue(account);
					this.connectConsumer.accept(account);
				};
				this.disconnectConsumer.accept(this.currentAccountConfigurationProperty.getValue());
			} else { // we are not yet connected
				this.showWaitMessage(true);
				this.currentAccountConfigurationProperty.setValue(account);
				this.connectConsumer.accept(account);
			}
		}
	}

	private void deleteRequested(AccountConfigurationInterface account) {
		if (new AutoCloseableAlert(AlertType.CONFIRMATION,
		                           getLocaleResource().getString("acc_delete_confirmation_title"),
		                           getLocaleResource().getString("acc_delete_confirmation_caption"),
		                           getLocaleResource().getString("acc_delete_confirmation"),
		                           ButtonType.CANCEL,
		                           ButtonType.YES, ButtonType.NO, ButtonType.CANCEL)
				.showAndWait()
				.filter(ButtonType.YES::equals).isPresent()) {
			if (account.equals(this.currentAccountConfigurationProperty.getValue())) {
				this.ownStatus.setValue(this.ownStatus.getItems().get(0));
				this.successfullDisconnectedCallback = (v) -> {
					this.successfullDisconnectedCallback = null;
					this.accountDeletedConsumer.accept(account);
				};
				this.showWaitMessage(false);
				this.disconnectConsumer.accept(account);
			} else {
				this.accountDeletedConsumer.accept(account);
			}
		}
	}

	private boolean confirmDisconnect(boolean implicit) {
		String content = getLocaleResource().getString(implicit ? "disconnect_confirmation_impl" : "disconnect_confirmation");
		String header  = getLocaleResource().getString(implicit ? "disconnect_confirmation_impl_caption" : "disconnect_confirmation_caption");

		AutoCloseableAlert alert = new AutoCloseableAlert(AlertType.CONFIRMATION,
		                                                  getLocaleResource().getString("disconnect_confirmation_title"), header, content,
		                                                  ButtonType.CANCEL,
		                                                  ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

		addCloseable(alert);

		return alert.showAndWait().filter(ButtonType.YES::equals).isPresent();
	}

	@FXML
	@SuppressWarnings("MethodMayBeStatic")
	private void menuInfoAction(ActionEvent event) {
		new AutoCloseableAlert(AlertType.INFORMATION,
		                       getLocaleResource().getString("license_alert_title"),
		                       getLocaleResource().getString("license_alert_header"),
		                       getLocaleResource().getString("license_alert_text"), ButtonType.OK)
				.showAndWait();
	}

	@FXML
	private void menuClose(ActionEvent event) {
		this.getStage().close();
	}

	@FXML
	private void menuConnectionStatus(ActionEvent event) {
		String errorMessage = this.lastError != null ? this.lastError.getValue() : "";
		errorMessage = (errorMessage == null || errorMessage.isEmpty()) ? getLocaleResource().getString("connection_status_no_last_error")
		                                                                : getLocaleResource().getString("connection_status_last_error") + ' ' + errorMessage;

		String serverInfo;

		if (this.currentAccountConfigurationProperty.getValue() != null) { // connected
			serverInfo = getLocaleResource().getString("connection_status_server") + ' ' + this.currentAccountConfigurationProperty.getValue().getAddress();
			try {
				String ip = InetAddress.getByName(this.currentAccountConfigurationProperty.getValue().getAddress()).getHostAddress();
				serverInfo += " (" + getLocaleResource().getString("connection_status_ip") + ' ' + ip + ')';
			} catch (UnknownHostException ignored) {
			}
			serverInfo += System.lineSeparator();
		} else {
			serverInfo = "";
		}

		String content = getLocaleResource().getString("connection_status_information") + ' ' +
		                 getLocaleResource().getString(this.currentAccountConfigurationProperty.getValue() != null ? "state_connected" : "state_not_connected") +
		                 System.lineSeparator() + serverInfo + errorMessage;

		new AutoCloseableAlert(AlertType.INFORMATION,
		                       getLocaleResource().getString("connection_status_title"),
		                       getLocaleResource().getString("connection_status_caption"),
		                       content,
		                       ButtonType.OK)
				.showAndWait();
	}

	@FXML
	private void menuPluginManager(ActionEvent event) {
		if (this.pluginManagerDialog != null)
			this.pluginManagerDialog.show();
	}

	@FXML
	private void menuAddAccount(ActionEvent event) {
		this.accountConfigurationDialog.show();
	}

	@FXML
	private void ownStatusChanged(ActionEvent event) {
		this.presenceChangeCallback.accept(this.ownStatus.getValue());
	}

	@FXML
	private void addContact(ActionEvent event) {
		this.addContactDialog.setCurrentContacts(this.currentContacts.stream().map(ContactInterface::getJid).map(JID::getBaseJID).collect(Collectors.toList()));
		this.addContactDialog.show();
	}

	@FXML
	private void btSend(ActionEvent event) {
		ContentInterface content = textInputWrapper.textContentProperty().getValue();
		if (content == null || content.getPlaintextRepresentation() == null || content.getPlaintextRepresentation().trim().isEmpty())
			return;

		this.messageSendRequestedCallback.accept(content, getSelectedContact());

		textInputWrapper.clear();
		this.typedText.remove(getSelectedContact());
		textInputWrapper.getControl().requestFocus();
	}

	@Override
	public void setAccountConfigurationDialog(AccountConfigurationDialogInterface accountConfigurationDialog) {
		this.accountConfigurationDialog = accountConfigurationDialog;
	}

	@Override
	public void setAddContactDialog(AddContactDialogInterface addContactDialog) {
		addCloseable(addContactDialog, false);
		this.addContactDialog = addContactDialog;
	}

	@Override
	public void setAccountConfigurations(ObservableList<? extends AccountConfigurationInterface> accountConfigurations) {
		accountConfigurations.addListener(this::updateAccountConfigurationMenu);
		this.accounts = accountConfigurations;
		this.updateAccountConfigurationMenu(null);
	}

	@Override
	public void setLocales(Map<String, Pair<String, Boolean>> locales) {
		this.menuLanguage.getItems().clear();

		final ToggleGroup group = new ToggleGroup();
		locales.forEach((id, entry) -> {
			RadioMenuItem item = new RadioMenuItem(entry.getKey() + " (" + id + ')');
			item.setToggleGroup(group);
			item.setSelected(entry.getValue());
			item.selectedProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue)
					setLocale(id);
				// don't change currentLocaleTag. Only change currentLocaleTag if everything else (language) changes as well
			});
			this.menuLanguage.getItems().add(item);

			if (entry.getValue())
				this.currentLocaleTag = id;
		});
	}

	@Override
	public StringProperty getSelectedLanguageProperty() {
		return this.selectedLanguage;
	}

	@Override
	public void setPluginManagerDialog(PluginManagerDialog pluginManagerDialog) {
		this.pluginManagerDialog = pluginManagerDialog;
	}

	@Override
	public void setLastErrorProperty(ReadOnlyStringProperty lastErrorProperty) {
		this.lastError = lastErrorProperty;
	}

	@Override
	public void setRoster(RosterInterface roster) {
		(this.rosterChangedConsumer == null ? (this.rosterChangedConsumer = buildRosterChangedConsumer()) : this.rosterChangedConsumer).accept(roster);
	}

	// never use different return values of multiple invocations of this method, unless you know exactly what this method does and what you want to achieve
	private Consumer<RosterInterface> buildRosterChangedConsumer() {
		final class Box<T> { // Boxing needed to update final value

			private T element;

			private Box() {
				this.element = null;
			}

			@Contract(pure = true)
			private T getElement() {
				return this.element;
			}

			private void setElement(T element) {
				this.element = element;
			}
		}

		final RosterObserverInterface observer = new RosterObserverInterface() {
			@Override
			public void contactCreated(RosterInterface roster, ContactInterface contact) {
				logger.trace("RosterObserver was notified: contactCreated event raised for roster " + System.identityHashCode(roster) +
				             " with contact " + System.identityHashCode(contact) + "; adding contact to roster list view");
				MainForm.this.currentContacts.add(contact);
				FXCollections.sort(MainForm.this.currentContacts, MainForm::contactSorter);
			}

			@Override
			public void contactRemoved(RosterInterface roster, ContactInterface contact) {
				logger.trace("RosterObserver was notified: contactRemoved event raised for roster " + System.identityHashCode(roster) +
				             " with contact " + System.identityHashCode(contact) + "; removing contact from roster list view");
				MainForm.this.currentContacts.remove(contact);
			}
		};
		final Box<RosterInterface> oldRosterBox = new Box<>();
		final Object               scopedLock   = new Object();

		return roster -> {
			synchronized (scopedLock) {
				RosterInterface oldRoster = oldRosterBox.getElement();
				//noinspection ObjectEquality
				if (oldRoster == roster)
					return;

				if (oldRoster != null) {
					logger.trace("MainForm: unregistering observer of previous roster");
					oldRoster.unregisterObserver(observer);
				}

				oldRosterBox.setElement(roster);

				if (roster != null) { // actual work is done *here*
					this.currentContacts.setAll(roster.getContacts());
					logger.trace("MainForm: registering roster observer");
					roster.registerObserver(observer);
					FXCollections.sort(MainForm.this.currentContacts, MainForm::contactSorter);
				} else {
					this.currentContacts.clear();
				}
			}
		};
	}

	@Override
	public void connectionEstablished() {
		this.mainContainer.setDisable(false);
		getCloseables().forEach(AutoCloseableFormInterface::close);
	}

	private void onDisconnected() {
		this.mainContainer.setDisable(true);
		this.currentAccountConfigurationProperty.setValue(null);
		this.setRoster(null);
	}

	@Override
	public void setOnConnectRequestedCallback(Consumer<AccountConfigurationInterface> consumer) {
		this.connectConsumer = consumer;
	}

	@Override
	public void setOnDisconnectRequestedCallback(Consumer<AccountConfigurationInterface> consumer) {
		this.disconnectConsumer = consumer;
	}

	@Override
	public void setPresenceChangeCallback(Consumer<PresenceValueInterface> newPresence) {
		this.presenceChangeCallback = newPresence;
	}

	@Override
	public void setContactDeletedConsumer(Consumer<ContactInterface> deletedContact) {
		this.contactDeletedConsumer = deletedContact;
	}

	@Override
	public void setSelectedContactChangedCallback(Consumer<ContactInterface> consumer) {
		this.contactListBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> consumer.accept(newValue));
	}

	@Override
	public void setMessageSendRequestedCallback(BiConsumer<ContentInterface, ContactInterface> consumer) {
		this.messageSendRequestedCallback = consumer;
	}

	@Override
	public void setMessageFetchFunction(BiFunction<AccountConfigurationInterface, ContactInterface, Collection<MessageInterface>> fetcher) {
		this.messageFetcher = fetcher;
	}

	@Override
	public void setAccountConfigurationDeletedConsumer(Consumer<AccountConfigurationInterface> consumer) {
		this.accountDeletedConsumer = consumer;
	}

	@Override
	public void showConnectionFailedMessage(ConnectionErrorReason reason) {
		getCloseables().forEach(AutoCloseableFormInterface::close);

		this.currentAccountConfigurationProperty.setValue(null);
		this.setRoster(null);

		AutoCloseableAlert alert = new AutoCloseableAlert(
				AlertType.ERROR,
				getLocaleResource().getString("connection_failed_title"), "", getLocaleResource().getString("connection_failed_reason_" + reason.name().toLowerCase()),
				ButtonType.OK);
		addCloseable(alert);
		alert.showAndWait();
	}

	@Override
	public void processDisconnectResult(boolean success) {
		logger.trace("Processing disconnect result: " + (success ? "successfully disconnected" : "disconnecting failed"));

		getCloseables().forEach(AutoCloseableFormInterface::close);

		if (success) {
			this.onDisconnected();
			if (this.successfullDisconnectedCallback != null)
				this.successfullDisconnectedCallback.accept(null);
		} else {
			AutoCloseableAlert alert = new AutoCloseableAlert(
					AlertType.ERROR,
					getLocaleResource().getString("disconnect_failed_title"),
					getLocaleResource().getString("disconnect_failed_caption"),
					getLocaleResource().getString("disconnect_failed"),
					ButtonType.OK);
			addCloseable(alert);
			alert.showAndWait();
		}
	}

	@Override
	public void onConnectionDied(AccountConfigurationInterface accountConfiguration) {
		this.onDisconnected();
		this.ownStatus.setValue(this.ownStatus.getItems().get(0));

		getCloseables().forEach(AutoCloseableFormInterface::close);

		AutoCloseableAlert alert;
		//noinspection VariableNotUsedInsideIf
		alert = this.successfullDisconnectedCallback == null
		        ? new AutoCloseableAlert(AlertType.ERROR,
		                                 getLocaleResource().getString("connection_died_title"),
		                                 getLocaleResource().getString("connection_died_caption"),
		                                 getLocaleResource().getString("connection_died"),
		                                 ButtonType.OK)
		        : new AutoCloseableAlert(AlertType.ERROR,
		                                 getLocaleResource().getString("connection_died_disconnect_cascade_title"),
		                                 getLocaleResource().getString("connection_died_disconnect_cascade_caption"),
		                                 getLocaleResource().getString("connection_died_disconnect_cascade"),
		                                 ButtonType.OK);

		addCloseable(alert);
		alert.showAndWait();
		if (this.successfullDisconnectedCallback != null)
			this.successfullDisconnectedCallback.accept(null);
	}

	@Override
	public void onMessageReceived(ContactInterface participatingContact, String sender, RenderableContentInterface msg,
	                              Instant otherSend, Instant serverReceived, Instant selfReceived) {
		if (getSelectedContact() != null && getSelectedContact().getJid().getBaseJID().equals(participatingContact.getJid().getBaseJID())) {
			Node newMessage = renderMessage(msg.getContent(),
			                                sender.equals(this.currentAccountConfigurationProperty.getValue().getJid()),
			                                otherSend);
			this.conversationPane.getChildren().add(newMessage);
		}
	}

	private ContactInterface getSelectedContact() {
		return this.contactListBox.getSelectionModel().getSelectedItem();
	}

	@Nullable
	private Node renderMessage(Node content, boolean ownMessage, Instant time) {
		if (content == null)
			return null;

		// content styling
		content.getStyleClass().add("message");

		// spacer
		VBox space = new VBox();
		space.setMinWidth(100);
		space.setAlignment(ownMessage ? Pos.BOTTOM_RIGHT : Pos.BOTTOM_LEFT);

		// time string
		Label timeText = new Label(DateTimeFormatter
				                           .ofLocalizedTime(FormatStyle.MEDIUM)
				                           .withLocale(Locale.forLanguageTag(this.currentLocaleTag))
				                           .withZone(ZoneId.systemDefault())
				                           .format(time));
		timeText.getStyleClass().add("timeText");
		space.getChildren().add(timeText);

		// combine & return
		HBox rendered = new HBox();
		rendered.setAlignment(ownMessage ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
		if (ownMessage)
			rendered.getChildren().addAll(space, content);
		else
			rendered.getChildren().addAll(content, space);

		return rendered;
	}

	private void showWaitMessage(boolean connect) {
		getCloseables().forEach(AutoCloseableFormInterface::close);

		AutoCloseableAlert alert = new AutoCloseableAlert(
				AlertType.INFORMATION,
				getLocaleResource().getString(connect ? "connection_wait_title" : "disconnect_wait_title"),
				getLocaleResource().getString(connect ? "connection_wait" : "disconnect_wait"),
				"",
				null);
		alert.setManualCloseProhibited(true);
		addCloseable(alert);
		alert.show();
	}

	@Override
	public void setPluginMenuItems(Collection<MenuItem> items) {
		if (items != null)
			this.menuPlugins.getItems().addAll(0, items);
	}

	@Override
	protected Size getDefaultSize() {
		return DEFAULT_SIZE;
	}
}
