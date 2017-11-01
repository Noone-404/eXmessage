package edu.kit.ss17.chatsys.team1.client.GUI.View;

import com.sun.javafx.collections.ObservableListWrapper;
import edu.kit.ss17.chatsys.team1.client.GUI.Internationalization.Locale;
import edu.kit.ss17.chatsys.team1.client.GUI.Internationalization.LocaleInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ControlWrapper.PlainRichTextFXWrapper;
import edu.kit.ss17.chatsys.team1.client.GUI.View.Form.*;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.*;
import edu.kit.ss17.chatsys.team1.client.Model.ContentInterface;
import edu.kit.ss17.chatsys.team1.client.Model.RenderableMessageInterface;
import edu.kit.ss17.chatsys.team1.client.Model.TextContentFragmentInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValueInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.Account;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.MenuItem;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 *
 */
public class ViewManager implements ViewManagerInterface, ApplicationViewManagerInterface {

	private static final Logger logger = LogManager.getLogger(APP_NAME);

	private static ViewManager instance;

	private final Object showFormLock = new Object();

	private volatile boolean makeMainFormVisible;

	// Callbacks
	private Consumer<JID>                                                       contactAdded;
	private Consumer<JID>                                                       contactRemoved;
	private Consumer<AccountConfigurationInterface>                             connect;
	private Function<AccountConfigurationInterface, CompletableFuture<Boolean>> disconnect;
	private Consumer<AccountConfigurationInterface>                             accountAdded;
	private Consumer<AccountConfigurationInterface>                             accountRemoved;
	private Consumer<AccountConfigurationInterface>                             accountEdited;
	private BiConsumer<ContentInterface, ContactInterface>                      sendMessage;
	private Consumer<ContactInterface>                                          selectedContactChanged;
	private Consumer<Void>                                                      inputChangedConsumer;
	private Consumer<Void>                                                      lastFormClosedConsumer;
	private Consumer<String>                                                    contactSearchConsumer;

	private Collection<MenuItem>                   pluginMenuItems;
	private Collection<Node>                       pluginControls;
	private Collection<PluginSetInterface>         networkPlugins;
	private Collection<PluginSetInterface>         nonNetworkPlugins;
	private ObjectProperty<PresenceValueInterface> currentPresenceProperty;
	private ReadOnlyStringWrapper                  lastError;

	private ObservableList<ObservableAccountConfigurationInterface>                                                                       accounts;
	private HashMap<edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.AccountConfigurationInterface, AccountConfigurationInterface> accountConfigurationMapper;

	private MainFormInterface                   mainForm;
	private AccountConfigurationDialogInterface accountConfigurationDialog;
	private AddContactDialogInterface           addContactDialog;

	private ViewManager() {
		this.currentPresenceProperty = new SimpleObjectProperty<>();
		this.lastError = new ReadOnlyStringWrapper("");
		this.accountConfigurationMapper = new HashMap<>();
	}

	public static ViewManager getInstance() {
		return (instance != null) ? instance : (instance = new ViewManager());
	}

	@Override
	public void setContactAddedConsumer(Consumer<JID> consumer) {
		this.contactAdded = consumer;
	}

	@Override
	public void setContactRemovedConsumer(Consumer<JID> consumer) {
		this.contactRemoved = consumer;
	}

	@Override
	public void setConnectRequestedConsumer(Consumer<AccountConfigurationInterface> consumer) {
		this.connect = consumer;
	}

	@Override
	public void setDisconnectFunction(Function<AccountConfigurationInterface, CompletableFuture<Boolean>> function) {
		this.disconnect = function;
	}

	@Override
	public void setSendMessageConsumer(BiConsumer<ContentInterface, ContactInterface> consumer) {
		this.sendMessage = consumer;
	}

	@Override
	public void setAccountAddedConsumer(Consumer<AccountConfigurationInterface> consumer) {
		this.accountAdded = consumer;
	}

	@Override
	public void setAccountRemovedConsumer(Consumer<AccountConfigurationInterface> consumer) {
		this.accountRemoved = consumer;
	}

	@Override
	public void setAccountChangedConsumer(Consumer<AccountConfigurationInterface> consumer) {
		this.accountEdited = consumer;
	}

	@Override
	public void setSelectedContactChangedConsumer(Consumer<ContactInterface> consumer) {
		this.selectedContactChanged = consumer;
	}

	@Override
	public void setInputChangedConsumer(Consumer<Void> consumer) {
		this.inputChangedConsumer = consumer;
	}

	@Override
	public void setLastFormClosedConsumer(Consumer<Void> consumer) {
		this.lastFormClosedConsumer = consumer;
	}

	@Override
	public void setContactSearchConsumer(Consumer<String> consumer) {
		this.contactSearchConsumer = consumer;
	}

	@Override
	public void onConnectionEstablished() {
		this.lastError.set(null);
		Platform.runLater(() -> getMainForm().connectionEstablished());
	}

	@Override
	public void onConnectionFailed(ConnectionErrorReason reason) {
		Platform.runLater(() -> getMainForm().showConnectionFailedMessage(reason));
	}

	@Override
	public void onConnectionDied(AccountConfigurationInterface killedAccount) {
		Platform.runLater(() -> getMainForm().onConnectionDied(
				this.accountConfigurationMapper.entrySet().stream().filter(entry -> entry.getValue().equals(killedAccount)).map(Entry::getKey).findFirst().orElse(null)));
	}

	@Override
	public void onMessageReceived(ContactInterface contact, RenderableMessageInterface message) {
		// TODO Design: message.getSender/receiver sollten JIDs sein - dafür ist die Abstrahierung da
		logger.trace("ViewManager: message received " + message.getID());
		Platform.runLater(() -> getMainForm().onMessageReceived(contact, message.getSender(), message, message.getClientSendDate(), message.getServerReceiveDate(),
		                                                        message.getClientReceiveDate()));
	}

	@Override
	public void setPluginMenuItems(Collection<MenuItem> items) {
		synchronized (this.showFormLock) {
			this.pluginMenuItems = items;
		}
	}

	@Override
	public void setPluginControls(Collection<Node> controls) {
		this.pluginControls = controls;
	}

	@Override
	public void setPlugins(Collection<PluginSetInterface> plugins, boolean isNetwork) {
		if (isNetwork)
			this.networkPlugins = plugins;
		else
			this.nonNetworkPlugins = plugins;
	}

	@Override
	public void setAccountList(Collection<AccountConfigurationInterface> accounts) {
		this.accounts = new ObservableListWrapper<>(
				accounts.stream().flatMap(a -> Stream

						/* Create ViewEntity AccountConfiguration */
						.of(new AccountConfiguration(
								a.getAddress(),
								a.getPort(),
								a.getProtocolName(),
								a.getAccount().getJid().getBaseJID(),
								a.getAccount().getPassword()))

						/* Create ObservableAccountConfiguration from ViewEntity */
						.map(ObservableAccountConfiguration::new)

						/* Add an error reporter */
						.peek(ac -> ac.setChangeErrorFeedbackCallback(this::accountConfigurationChangeErrorCallback))

						/* Add listener */
						.peek(ac -> ac.addListener(observable -> accountConfigurationChangeListener(observable, ac, a)))

						/* Get the listening-enabled AccountConfiguration object */
						.map(ObservableAccountConfiguration::getValue)

						/* Store references for callbacks */
						.peek(ac -> this.accountConfigurationMapper.put(ac, a)))

					    /* Make a list */
					    .collect(Collectors.toList()),

				param -> new Observable[] {param.getObservable()}
		);
	}

	@Override
	public ObjectProperty<PresenceValueInterface> currentPresenceProperty() {
		return this.currentPresenceProperty;
	}

	@Override
	public void provideSearchResults(Collection<String> results) {
		if (this.addContactDialog == null)
			return;

		Platform.runLater(() -> this.addContactDialog.provideSearchResults(results));
	}

	@Override
	public TextContentFragmentInterface getTextContent() {
		// TODO
		return null;
	}

	@Override
	public void setTextContent(TextContentFragmentInterface content) {
		// TODO
	}

	@Override
	public IndexRange getIndexRange() {
		// TODO
		return null;
	}

	@Override
	public void setIndexRange(IndexRange indexRange) {
		// TODO
	}

	@Override
	public void buildViews() {
		ViewManagerApplication.setAssociatedViewManager(this);
		// we have to start a JavaFX-Application to create Stages.
		// JavaFX-Applications run in their own thread until the application is exited, e.g. by the user closing the last Stage.
		// Thus the application is run within its own thread.
		new Thread(() -> Application.launch(ViewManagerApplication.class)).start();
	}

	@Override
	public void showMainWindow() {
		// application startup might occur after showMainWindow() has been called
		synchronized (this.showFormLock) {
			if (this.mainForm != null)
				if (Platform.isFxApplicationThread())
					// could technically use runLater here as well, but showing main form is assumed to be of higher priority that any queued tasks
					this.mainForm.show();
				else
					Platform.runLater(this.mainForm::show);
			else
				this.makeMainFormVisible = true;
		}
	}

	@Override
	public void setLastErrorMessage(String errorMessage) {
		this.lastError.setValue(errorMessage);
	}

	@Override
	public void setRoster(RosterInterface roster) {
		getMainForm().setRoster(roster);
	}

	@Override
	public LocaleInterface getCurrentLocale() {
		return Locale.getCurrent(true);
	}

	@Override
	public void performStaticInitialisation() {
		ControlBase.setLocaleResource(getCurrentLocale().getBundle());        // set the language for all views globally
		MainForm.setTextInputWrapper(new PlainRichTextFXWrapper());
	}

	private MainFormInterface getMainForm() {
		synchronized (this.showFormLock) {
			return this.mainForm;
		}
	}

	@Override
	public void performInitialisation(MainFormInterface mainForm) {
		initMainForm(mainForm);
		synchronized (this.showFormLock) {
			this.mainForm = mainForm;
			this.mainForm.setPluginMenuItems(this.pluginMenuItems);

			if (this.makeMainFormVisible)
				mainForm.show();
		}
	}

	private void accountConfigurationChangeListener(Observable observable, ObservableAccountConfiguration ac, AccountConfigurationInterface a) {
		try {
			a.getAccount().setJid(new JID(ac.getValue().getJid()));
		} catch (InvalidJIDException e) {
			((ObservableAccountConfiguration) observable).changeFailedFeedback(e);
			return;
		}
		a.getAccount().setPassword(ac.getValue().getPassword());
		a.setAddress(ac.getValue().getAddress());
		a.setPort(ac.getValue().getPort());
		a.setProtocolName(ac.getValue().getProtocol());

		this.accountEdited.accept(a);
	}

	private void accountConfigurationChangeErrorCallback(Exception e) {
		this.accountConfigurationDialog.showConfigurationChangeFailedMessage(e);
	}

	private void initMainForm(MainFormInterface mainForm) {
		// mainForm
		mainForm.setLocales(Locale.getAll().stream().collect(Collectors.toMap(LocaleInterface::getIdentifier, loc -> new Pair<>(loc.getDisplayName(), loc.isCurrent()))));
		mainForm.getSelectedLanguageProperty().addListener(
				(observable, oldValue, newValue) ->
						Locale.getAll().stream().filter(locale -> locale.getIdentifier().equals(newValue)).findFirst().ifPresent(LocaleInterface::makeCurrent));

		mainForm.setLastErrorProperty(this.lastError.getReadOnlyProperty());

		mainForm.setPresenceChangeCallback(this.currentPresenceProperty::setValue);
		mainForm.setOnConnectRequestedCallback(ac -> this.connect.accept(this.accountConfigurationMapper.get(ac)));
		mainForm.setOnDisconnectRequestedCallback(ac -> this.disconnect
				.apply(this.accountConfigurationMapper.get(ac))
				.thenAcceptAsync(mainForm::processDisconnectResult, Platform::runLater));

		mainForm.setAccountConfigurationDeletedConsumer(
				accountConfigurationInterface -> {
					this.accountRemoved.accept(this.accountConfigurationMapper.get(accountConfigurationInterface));
					//noinspection SuspiciousMethodCalls
					this.accounts.remove(accountConfigurationInterface);
				});
		mainForm.setSelectedContactChangedCallback(this.selectedContactChanged);
		mainForm.setContactDeletedConsumer(contact -> this.contactRemoved.accept(contact.getJid()));
		mainForm.setMessageSendRequestedCallback(this.sendMessage);
		mainForm.setMessageFetchFunction((accountConfigurationInterface, contactInterface) -> {
			AccountConfigurationInterface translated = this.accountConfigurationMapper.get(accountConfigurationInterface);
			if (translated == null)
				return new ArrayList<>();

			return contactInterface.getMessages(translated);
		});

		// Plugin manager
		PluginManagerDialog pluginManagerDialog = new PluginManagerDialog();
		pluginManagerDialog.setPlugins(Stream.concat(
				this.networkPlugins.stream().map(pluginSetInterface -> new Plugin(pluginSetInterface, true)),
				this.nonNetworkPlugins.stream().map(pluginSetInterface -> new Plugin(pluginSetInterface, false))
		                                            ).collect(Collectors.toList()));
		mainForm.setPluginManagerDialog(pluginManagerDialog);

		// account configuration
		mainForm.setAccountConfigurations(this.accounts);
		this.accountConfigurationDialog = new AccountConfigurationDialog();
		this.accountConfigurationDialog.setAvailableProtocols(this.networkPlugins.stream().map(PluginSetInterface::getName).collect(Collectors.toList()));

		this.accountConfigurationDialog.setAccountConfigurationAddedConsumer(
				accountConfiguration -> {
					edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfiguration sharedAC = new edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfiguration(
							new Account(new JID(accountConfiguration.getJid()), accountConfiguration.getPassword()),
							accountConfiguration.getProtocol(),
							accountConfiguration.getAddress(),
							accountConfiguration.getPort()
					);
					this.accountAdded.accept(sharedAC);

					ObservableAccountConfiguration observableAccountConfiguration = new ObservableAccountConfiguration(accountConfiguration);
					observableAccountConfiguration.setChangeErrorFeedbackCallback(this::accountConfigurationChangeErrorCallback);
					observableAccountConfiguration.addListener(observable -> accountConfigurationChangeListener(observable, observableAccountConfiguration, sharedAC));

					ObservableAccountConfigurationInterface aci = observableAccountConfiguration.getValue();
					this.accountConfigurationMapper.put(aci, sharedAC);
					this.accounts.add(aci);
				});
		mainForm.setAccountConfigurationDialog(this.accountConfigurationDialog);

		// contact search
		this.addContactDialog = new AddContactDialog();
		this.addContactDialog.setSearchRequestCallback(this.contactSearchConsumer);
		this.addContactDialog.setContactAddCallback(s -> this.contactAdded.accept(new JID(s)));
		mainForm.setAddContactDialog(this.addContactDialog);
	}

	@Override
	@Nullable
	public Consumer<Void> getLastFormClosedConsumer() {
		return this.lastFormClosedConsumer;
	}

}
