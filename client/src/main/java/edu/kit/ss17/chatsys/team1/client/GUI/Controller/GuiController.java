package edu.kit.ss17.chatsys.team1.client.GUI.Controller;

import edu.kit.ss17.chatsys.team1.client.AccountManager.AccountManagerInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.Internationalization.Locale;
import edu.kit.ss17.chatsys.team1.client.GUI.Plugin.GuiMenuPluginInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.Plugin.GuiMessageContentPluginInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.Plugin.GuiMessagePluginInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.Plugin.GuiPluginInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.Plugin.PluginAccessor.GuiPluginAccessor;
import edu.kit.ss17.chatsys.team1.client.GUI.Plugin.PluginAccessor.GuiPluginAccessorInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.ConnectionErrorReason;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewManager;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewManagerInterface;
import edu.kit.ss17.chatsys.team1.client.Model.ContentInterface;
import edu.kit.ss17.chatsys.team1.client.Model.RenderableMessageInterface;
import edu.kit.ss17.chatsys.team1.client.RosterManager.RosterManagerInterface;
import edu.kit.ss17.chatsys.team1.client.Storage.Storage;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.MessageObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValue;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValueInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;


public class GuiController implements GuiControllerInterface {

	private static final Logger logger = LogManager.getLogger(APP_NAME);

	private static final Object disconnectAccountLock = new Object();

	private static GuiController instance;

	private GuiPluginAccessorInterface            guiAccessor;
	private RosterInterface                       roster;
	private RosterManagerInterface                rosterManager;
	private AccountManagerInterface               accountManager;
	private MessageObserverInterface              contactObserver;
	private RosterObserverInterface               rosterObserver;
	private AccountConfigurationInterface         activeAccount;
	private Collection<GuiObserverInterface>      guiObservers;
	private Collection<MenuItem>                  pluginMenuItems;
	private Collection<Node>                      pluginExtraControls;
	private Collection<PluginSetInterface>        networkPlugins;
	private Collection<PluginSetInterface>        nonNetworkPlugins;
	private Collection<GuiMessagePluginInterface> messagePlugins;
	private ViewManagerInterface                  viewManager;

	private CompletableFuture<Boolean>    disconnectResult;
	private AccountConfigurationInterface disconnectAccount;

	private GuiController() {
		// init fields
		this.guiObservers = new ArrayList<>();
		this.pluginMenuItems = new ArrayList<>();
		this.pluginExtraControls = new ArrayList<>();

		this.networkPlugins = new ArrayList<>();
		this.nonNetworkPlugins = new ArrayList<>();
		this.messagePlugins = new ArrayList<>();

		this.viewManager = ViewManager.getInstance();

		// init viewManager callbacks / consumers
		this.viewManager.setContactAddedConsumer(this::onContactAdded);
		this.viewManager.setContactRemovedConsumer(this::onContactRemoved);
		this.viewManager.setConnectRequestedConsumer(this::onConnectRequested);
		this.viewManager.setDisconnectFunction(this::onDisconnectRequested);
		this.viewManager.setSendMessageConsumer(this::sendMessage);
		this.viewManager.setSelectedContactChangedConsumer(this::selectedContactChanged);
		this.viewManager.setInputChangedConsumer(this::inputChanged);
		this.viewManager.setContactSearchConsumer(this::searchForContact);
		this.viewManager.setLastFormClosedConsumer(this::onWindowClose);

		this.viewManager.currentPresenceProperty().addListener((x, y, z) -> onCurrentPresenceChanged(z));

		this.guiAccessor = new GuiPluginAccessor(/* General events        */ this::registerObserver,
		                                         /* Access to TextContent */ this.viewManager::setTextContent, this.viewManager::getTextContent,
		                                         /* Access to IndexRange  */ this.viewManager::setIndexRange, this.viewManager::getIndexRange,
		                                         /* Send message          */ this::sendMessage);

		Locale.setStorage(Storage.getInstance());

		// init observers
		this.contactObserver = new MessageObserverInterface() {
			@Override
			public void messageAdded(ContactInterface contact, MessageInterface message) {
				logger.trace("GuiController@MessageObserver: message '" + message.getID() + "' received for contact " + System.identityHashCode(contact));

				if (!(message instanceof RenderableMessageInterface))
					return;

				// Forward message to plugins
				for (GuiMessagePluginInterface plugin : GuiController.this.messagePlugins) {
					plugin.messageAdded(contact, message);
				}

				GuiController.this.viewManager.onMessageReceived(contact, (RenderableMessageInterface) message);
			}

			@Override
			public void messageChanged(ContactInterface contact, MessageInterface message) {
				if (!(message instanceof RenderableMessageInterface))
					return;

				RenderableMessageInterface msg = (RenderableMessageInterface) message;
				// TODO if contact is currently selected, re-render message
			}
		};

		this.rosterObserver = new RosterObserverInterface() {
			@Override
			public void contactCreated(RosterInterface roster, ContactInterface contact) {
				GuiController.this.messagePlugins.forEach(contact::registerObserver);
				// note order of registration
				contact.registerObserver(GuiController.this.contactObserver);

				registerPropertyObservers(contact);
			}

			@Override
			public void contactRemoved(RosterInterface roster, ContactInterface contact) {
				GuiController.this.messagePlugins.forEach(contact::unregisterObserver);
				contact.unregisterObserver(GuiController.this.contactObserver);
			}
		};
	}

	public static GuiControllerInterface getInstance() {
		return instance != null ? instance : (instance = new GuiController());
	}

	private void searchForContact(String s) {
		this.guiObservers.forEach(guiObserverInterface -> guiObserverInterface.onSearchRequestPerformed(this.activeAccount, s));
	}

	private void selectedContactChanged(ContactInterface contact) {
		this.guiAccessor.setSelectedContact(contact);
		this.guiObservers.forEach(guiObserverInterface -> guiObserverInterface.onSelectedContactChanged(contact));
	}

	private void onCurrentPresenceChanged(PresenceValueInterface presence) {
		this.guiObservers.forEach(guiObserverInterface -> guiObserverInterface.onPresenceChanged(this.activeAccount, presence));
	}

	private CompletableFuture<Boolean> onDisconnectRequested(AccountConfigurationInterface accountConfiguration) {
		logger.trace("Disconnect requested");
		this.disconnectResult = new CompletableFuture<>();
		synchronized (disconnectAccountLock) {
			this.disconnectAccount = accountConfiguration;
		}
		(new ArrayList<>(this.guiObservers)).forEach(guiObserverInterface -> guiObserverInterface.onDisconnect(accountConfiguration));
		return this.disconnectResult;
	}

	private void onWindowClose(@SuppressWarnings("unused") Void v) {
		if (this.activeAccount != null) {
			this.onCurrentPresenceChanged(PresenceValue.OFFLINE);
			this.onDisconnectRequested(this.activeAccount);
		}
	}

	@Override
	public void initializeDependentComponents(RosterManagerInterface rosterManager, AccountManagerInterface accountManager) {
		this.rosterManager = rosterManager;
		this.accountManager = accountManager;
		this.viewManager.setAccountAddedConsumer(this.accountManager::saveAccount);
		this.viewManager.setAccountRemovedConsumer(this.accountManager::deleteAccount);
		this.viewManager.setAccountChangedConsumer(this.accountManager::saveAccount);
	}

	@Override
	public void addGlobalPlugin(PluginSetInterface plugin, boolean network) {
		(network ? this.networkPlugins : this.nonNetworkPlugins).add(plugin);
	}

	@Override
	public void showMainWindow() {
		initGui();
		this.viewManager.showMainWindow();
	}

	private void initGui() {
		this.viewManager.setPlugins(this.networkPlugins, true);
		this.viewManager.setPlugins(this.nonNetworkPlugins, false);
		this.viewManager.setAccountList(this.accountManager.getAccountList());
		this.viewManager.buildViews();
	}

	@Override
	public void setLastErrorMessage(String error) {
		this.viewManager.setLastErrorMessage(error);
	}

	@Override
	public void provideSearchResults(Collection<String> results) {
		this.viewManager.provideSearchResults(results);
	}

	@Override
	public void onConnectionSuccess() {	this.viewManager.onConnectionEstablished();	}

	@Override
	public void onConnectionFailed(ConnectionErrorReason reason) {
		this.viewManager.onConnectionFailed(reason);
	}

	@Override
	public void onDisconnected() {
		processDisconnection(true);
	}

	private void processDisconnection(boolean anticipated) {
		logger.trace("Processing disconnect: was " + (anticipated ? "expected" : "not expected"));
		this.activeAccount = null; // currently not supporting multiple accounts simultaneously
		this.guiAccessor.setLoggedInJID(null);
		synchronized (disconnectAccountLock) {
			if (this.disconnectAccount != null)
				this.rosterManager.getRoster(this.disconnectAccount).unregisterObserver(this.rosterObserver);
			else
				this.roster.unregisterObserver(this.rosterObserver);
			this.messagePlugins.forEach(this.roster::unregisterObserver);
		}
		if (anticipated && this.disconnectResult != null) {
			logger.trace("Keeping disconnectResult promise with value true");
			this.disconnectResult.complete(true);
		} else if (anticipated) {
			logger.trace("Not keeping disconnectResult promise: disconnectResult is null");
		} else {
			logger.trace("Not keeping disconnectResult promise: disconnect was not expected");
		}
	}

	@Override
	public void onConnectionDied(AccountConfigurationInterface killedAccount) {
		processDisconnection(false);
		this.viewManager.onConnectionDied(killedAccount);
	}

	private void onConnectRequested(AccountConfigurationInterface account) {
		this.activeAccount = account;
		this.guiAccessor.setLoggedInJID(account.getAccount().getJid());

		this.roster = this.rosterManager.getRoster(account);
		this.roster.registerObserver(this.rosterObserver);
		this.messagePlugins.forEach(this.roster::registerObserver);

		Collection<JID> hitList = new ArrayList<>();
		for (final ContactInterface contact : this.roster.getContacts()) {
			if (contact.isPersistent()) {
				if (contact.presenceProperty().getValue() != PresenceValue.OFFLINE)
					contact.presenceProperty().setValue(PresenceValue.OFFLINE);
				registerPropertyObservers(contact);
				contact.registerObserver(this.contactObserver);
			} else
				hitList.add(contact.getJid());
		}
		hitList.forEach(this.roster::deleteContact);

		this.viewManager.setRoster(this.roster);

		(new ArrayList<>(this.guiObservers)).forEach(o -> o.onConnect(account, this.roster));
	}

	private void registerPropertyObservers(ContactInterface contact) {
		logger.trace("Registering property listeners for contact " + System.identityHashCode(contact));
		contact.aliasProperty().addListener(observable -> {
			if (contact.isPersistent())
				this.rosterManager.saveRoster(contact.getRoster());
		});
		contact.persistenceProperty().addListener(observable -> {
			this.rosterManager.saveRoster(contact.getRoster());
			if (contact.isPersistent())
				this.guiObservers.forEach(guiObserverInterface -> guiObserverInterface.onPersistentContactAdded(this.activeAccount, contact));
		});
	}

	/**
	 * Sends the given message.
	 */
	private void sendMessage(ContentInterface content, ContactInterface contact) {
		this.guiObservers.forEach(guiObserverInterface -> guiObserverInterface.onInputComposed(this.activeAccount, contact, content));
	}

	private void inputChanged(@SuppressWarnings("unused") Void v) {
		this.guiObservers.forEach(GuiObserverInterface::onInputChanged);
	}

	private void onContactRemoved(JID jid) {
		this.roster.deleteContact(jid);
		this.rosterManager.saveRoster(this.roster);
	}

	/**
	 * Called when a contact gets added from the AddContactForm.
	 *
	 * @param jid JID of the new contact
	 */
	private void onContactAdded(JID jid) {
		logger.trace("User invoked addition of '" + jid.getBaseJID() + "' to roster");
		ContactInterface contact = this.roster.getContact(jid);

		if (contact != null && !contact.isPersistent())
			contact.makePersistent();
		else if (contact == null) {
			final ContactInterface fContact = this.roster.createContact(jid, null, true);
			this.guiObservers.forEach(guiObserverInterface -> guiObserverInterface.onPersistentContactAdded(this.activeAccount, fContact));
		}

		this.rosterManager.saveRoster(this.roster);
	}


	@Override
	public void registerObserver(GuiObserverInterface observer) {
		if (!this.guiObservers.contains(observer))
			this.guiObservers.add(observer);
	}

	@Override
	public void unregisterObserver(GuiObserverInterface observer) {
		if (this.guiObservers.contains(observer))
			this.guiObservers.remove(observer);
	}

	@Override
	public void tryRegisterPlugin(PluginInterface plugin, boolean lastPlugin) {
		try {
			if (!(plugin instanceof GuiPluginInterface))
				return;

			((GuiPluginInterface) plugin).setGuiAccessor(this.guiAccessor);

			if (plugin instanceof GuiMessagePluginInterface)
				this.messagePlugins.add((GuiMessagePluginInterface) plugin);

			// no if-else!
			if (plugin instanceof GuiMenuPluginInterface)
				this.pluginMenuItems.addAll(((GuiMenuPluginInterface) plugin).getMenuItems());

			if (plugin instanceof GuiMessageContentPluginInterface)
				this.pluginExtraControls.addAll(((GuiMessageContentPluginInterface) plugin).getInputControls());

		} finally {
			if (lastPlugin)
				performPluginInitialisation();
		}
	}

	private void performPluginInitialisation() {
		this.viewManager.setPluginMenuItems(this.pluginMenuItems);
		this.viewManager.setPluginControls(this.pluginExtraControls);
	}
}
