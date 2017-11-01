package edu.kit.ss17.chatsys.team1.client.Controller;

import edu.kit.ss17.chatsys.team1.client.GUI.Controller.GuiController;
import edu.kit.ss17.chatsys.team1.client.GUI.Controller.GuiControllerInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.Controller.GuiObserverInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.ConnectionErrorReason;
import edu.kit.ss17.chatsys.team1.client.Model.ContentInterface;
import edu.kit.ss17.chatsys.team1.client.Model.RenderablePlaintextMessageStanza;
import edu.kit.ss17.chatsys.team1.client.Session.SessionFactory;
import edu.kit.ss17.chatsys.team1.client.Session.SessionInterface;
import edu.kit.ss17.chatsys.team1.client.StreamProcessor.ClientStreamProcessorFactory;
import edu.kit.ss17.chatsys.team1.client.StreamProcessor.Entities.JidSearchRequest;
import edu.kit.ss17.chatsys.team1.client.StreamProcessor.Entities.JidSearchRequestInterface;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement.ConnectionLostObserverInterface.ConnectionLostReason;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement.NetworkStackFactory;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement.NetworkStackInterface;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkConnectionIdentificationInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginManagerFactory;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginManagerInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StreamErrorElement;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorManagerFactory;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorManagerInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolTranslator.ProtocolTranslatorFactory;
import edu.kit.ss17.chatsys.team1.shared.ProtocolTranslator.ProtocolTranslatorInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.*;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Constants;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.StreamProcessorInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainDataProcessor;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainUpperDataProcessor;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 * Our client connection stack.
 */
public class ConnectionStack extends ChainUpperDataProcessor<DataElementInterface> implements ConnectionStackInterface {

	private static final Logger logger       = LogManager.getLogger(APP_NAME);
	private final        Object searchIdLock = new Object();

	private final ControllerInterface           controller;
	private final ErrorManagerInterface         errorManager;
	private final PluginManagerInterface        pluginManager;
	private final NetworkStackInterface         networkStack;
	private final ProtocolTranslatorInterface   translator;
	private final StreamProcessorInterface      streamProcessor;
	private final SessionInterface              session;
	private final AccountConfigurationInterface accountConfiguration;
	private final GuiControllerInterface        gui;
	private final RosterInterface               roster;
	private       GuiObserverInterface          guiObserver;
	private       JidSearchRequestInterface     lastSearchRequest;
	private       boolean                       isDisconnected;

	ConnectionStack(AccountConfigurationInterface accountConfiguration, GuiControllerInterface gui, RosterInterface roster) throws IOException {
		this.accountConfiguration = accountConfiguration;
		this.gui = gui;
		this.roster = roster;

		this.controller = Controller.getInstance();
		this.errorManager = ErrorManagerFactory.make();
		this.networkStack = NetworkStackFactory.make();
		this.networkStack.setProtocol(accountConfiguration.getProtocolName());
		this.session = SessionFactory.make();
		this.translator = ProtocolTranslatorFactory.make();
		this.streamProcessor = ClientStreamProcessorFactory.make();
		this.streamProcessor.setSession(this.session);
		this.pluginManager = PluginManagerFactory.make();

		initPluginManager();
		initErrorManager();

		registerGuiObserver();
		registerErrorObserver();

		initChain();

		// Initiate connection
		this.networkStack.setConnectionInfo(new ConnectionIdentification(accountConfiguration.getAddress(), accountConfiguration.getPort()));

		this.networkStack.registerConnectionStateObserver(this::processConnectionLoss);

		try {
			this.networkStack.connect();
		} catch (IOException e) {
			this.gui.unregisterObserver(this.guiObserver);
			throw e;
		}

		this.session.initSession(accountConfiguration.getAccount());
	}

	@Override
	public void negotiationFinished(boolean success) {
		logger.trace("Negotiation " + (success ? "successful" : "failed"));
		if (success) {
			onConnected();
		} else { // Soft disconnect.
			// TODO: irgendwie mitteilen, wenn Negotiation bspw. aufgrund von fehlenden Required-Features fehlschlägt
			this.gui.onConnectionFailed(ConnectionErrorReason.LOGIN);
			this.disconnect();
		}
	}

	private void onConnected() {
		this.gui.onConnectionSuccess(); // Tell GUI we're ready to chat.

		ConnectionStack.this.session.setPresence(PresenceValue.OFFLINE); // we're offline per default

		// ask all contacts for their presence
		this.roster.getContacts().forEach(contact -> {
			if (!contact.isPersistent()) // dont probe non-persistent contacts - otherwise they could know we are online
				return;
			PresenceInterface stanza = new Presence();
			stanza.setReceiver(contact.getJid().toString());
			stanza.setType("probe");
			sendStanzaToStreamProcessor(stanza);
		});
	}

	/**
	 * Tell the components which one is lower and upper.
	 */
	private void initChain() {
		ChainDataProcessor.link(this.networkStack, this.translator);
		ChainDataProcessor.link(this.translator, this.streamProcessor);
		ChainDataProcessor.link(this.streamProcessor, this);
		this.streamProcessor.setConnectionStackBase(this); // To enable StreamProcessor to switch upper between Session and this.

		this.session.setLower(this.streamProcessor);
		this.session.setConnectionStack(this);
	}

	/**
	 * Tell the plugin manager which components are pluginable.
	 */
	private void initPluginManager() {
		this.pluginManager.registerPluginable(this.session);
		this.pluginManager.registerPluginable(this.streamProcessor);
		this.pluginManager.registerPluginable(this.networkStack);

		this.pluginManager.registerPlugins();
	}

	/**
	 * Tells the error manager which components to observe.
	 */
	private void initErrorManager() {
		this.errorManager.setStreamProcessor(this.streamProcessor);
		this.errorManager.registerObserverAt(this.session);
		this.errorManager.registerObserverAt(this.streamProcessor);
		this.errorManager.registerObserverAt(this.translator);
	}

	/**
	 * Registers an observer at the GUI controller.
	 */
	private void registerGuiObserver() {
		this.gui.registerObserver(this.guiObserver = new GuiObserverInterface() {
			@Override
			public void onDisconnect(AccountConfigurationInterface account) {
				if (isResponsibleFor(account)) {
					logger.trace("GUI requested to disconnect");
					disconnect();
				}
			}

			@Override
			public void onInputComposed(AccountConfigurationInterface account, ContactInterface contact, ContentInterface input) {
				if (ConnectionStack.this.isResponsibleFor(account)) {
					MessageInterface msg = new RenderablePlaintextMessageStanza();
					// wenn ExtendedContent berücksichtigt werden soll, sollte nicht mehr RenderablePlaintextMessageStanza verwendet werden
					// msg.setExtendedContent(input.getExtendedContent());
					msg.setPlaintextRepresentation(input.getPlaintextRepresentation());
					msg.setReceiver(contact.getJid().toString());
					sendStanzaToStreamProcessor(msg);
					contact.addMessage(account, msg);
				}

			}

			@Override
			public void onPresenceChanged(AccountConfigurationInterface account, PresenceValueInterface presence) {
				if (isResponsibleFor(account)) {
					ConnectionStack.this.session.setPresence(presence);
					for (ContactInterface contact : ConnectionStack.this.roster.getContacts()) {
						if (!contact.isPersistent())
							continue;

						PresenceInterface stanza = new Presence();
						stanza.setReceiver(contact.getJid().toString());
						stanza.setPresence(presence);
						stanza.setType(presence == PresenceValue.OFFLINE ? Constants.UNAVAILABLE : Constants.AVAILABLE);
						sendStanzaToStreamProcessor(stanza);
					}
				}
			}

			@Override
			public void onSearchRequestPerformed(AccountConfigurationInterface account, String searchString) {
				if (!isResponsibleFor(account))
					return;

				if (searchString == null || searchString.isEmpty()) {
					ConnectionStack.this.gui.provideSearchResults(new ArrayList<>());
					return;
				}

				JidSearchRequestInterface request = new JidSearchRequest(searchString);
				synchronized (ConnectionStack.this.searchIdLock) {
					ConnectionStack.this.lastSearchRequest = request;
				}
				sendStanzaToStreamProcessor(request);
			}

			@Override
			public void onPersistentContactAdded(AccountConfigurationInterface account, ContactInterface contact) {
				if (!isResponsibleFor(account))
					return;

				// probe for contact's presence
				PresenceInterface probe = new Presence();
				probe.setReceiver(contact.getJid().toString());
				probe.setType("probe");
				sendStanzaToStreamProcessor(probe);

				// send own presence
				PresenceInterface stanza = new Presence();
				stanza.setReceiver(contact.getJid().toString());
				PresenceValueInterface presence = ConnectionStack.this.session.getPresence();
				stanza.setPresence(presence);
				stanza.setType(presence == PresenceValue.OFFLINE ? Constants.UNAVAILABLE : Constants.AVAILABLE);
				sendStanzaToStreamProcessor(stanza);
			}
		});
	}

	/**
	 * Registers an error observer at the error manager
	 */
	private void registerErrorObserver() {
		this.errorManager.registerObserver(error -> {
			// TODO: GUI über genaueren Error benachrichtigen, damit evtl eine Fehlermeldung angezeigt werden kann
			// TODO: ErrorObserver genauso wie GUI-Observer unregistrieren, wenn Construktor failed oder disconnect durchgeführt wird
			ConnectionStack.this.gui.setLastErrorMessage(error.getErrorMessage());

			if (error.isFatal()) {
				logger.trace("Fatal error occured: " + error.getErrorMessage());
				disconnect();
			}
		});
	}

	/**
	 * Checks if this configuration stack is responsible for the given accountConfiguration configuration.
	 */
	private boolean isResponsibleFor(AccountConfigurationInterface account) {
		return this.getAccountConfiguration().equals(account);
	}

	private void disconnect() {
		if (this.session.isDisconnecting()) {
			logger.trace("Disconnecting: close connection");
			// We've already took action before and are now allowed to close the connection.
			this.networkStack.disconnect();
		} else {
			// Tell the opponent we want to disconnect.
			logger.trace("Disconnecting: request session to send closing stream tag");
			this.session.terminateSession();
		}
		this.gui.unregisterObserver(this.guiObserver);
	}


	/**
	 * Central point to send stanzas to the stream processor. Decorates the stanza with mandatory meta information.
	 */
	private void sendStanzaToStreamProcessor(StanzaInterface stanza) {
		stanza.setID(UUID.randomUUID().toString());
		stanza.setSender(getAccountConfiguration().getAccount().getJid().toString());
		stanza.setClientSendDate(Instant.now());

		this.streamProcessor.pushDataDown(stanza);
	}

	@Override
	public AccountConfigurationInterface getAccountConfiguration() {
		return this.accountConfiguration;
	}


	/**
	 * Gets called if ConnectionStack receives data from stream processor.
	 */
	@Override
	public void pushDataUp(DataElementInterface data) {
		if (data instanceof MessageInterface) { // TODO: Design
			this.processIncomingMessage((MessageInterface) data);
		} else if (data instanceof PresenceInterface) {
			this.processIncomingPresence((PresenceInterface) data);
		} else if (data instanceof IQInterface) {
			this.processIncomingIQ((IQInterface) data);
		} else if (data instanceof StreamErrorElement) {
			this.disconnect();
		} else if (data instanceof StreamFooterInterface) {
			logger.trace("Stream footer received");
			this.isDisconnected = true;
			this.disconnect();
		}
	}

	private void processIncomingMessage(MessageInterface message) {
		JID receiver;
		JID sender;

		try {
			receiver = new JID(message.getReceiver());
			sender = new JID(message.getSender());
		} catch (InvalidJIDException ignored) {
			return;
		}

		if (!receiver.equals(getAccountConfiguration().getAccount().getJid()))
			return; // drop it

		ContactInterface contact = this.roster.getContact(sender);
		if (contact == null) {
			logger.trace("Contact does not yet exist. Create new non-persistent contact.");
			contact = this.roster.createContact(sender, null, false);
			// Do not probe for presence of newly created contact - otherwise they could know that we are currently online
		}
		logger.trace("Adding incoming message for contact " + System.identityHashCode(contact));
		contact.addMessage(getAccountConfiguration(), message);
	}

	private void processIncomingPresence(PresenceInterface presence) {
		JID receiver;
		JID sender;
		try {
			receiver = new JID(presence.getReceiver());
			sender = new JID(presence.getSender());
		} catch (InvalidJIDException ignored) {
			return; // drop it
		}

		if (!receiver.equals(getAccountConfiguration().getAccount().getJid()))
			return;// drop it

		logger.trace("Presence from '" + sender + "' received");
		ContactInterface contact = this.roster.getContact(sender);
		if (contact == null) {
			return;// drop it
		}

		if (presence.getType().equals("probe")) {
			if (!contact.isPersistent())
				return; // dont answer to probes from non-persistent contacts

			PresenceInterface response = new Presence();
			response.setReceiver(contact.getJid().toString());
			PresenceValueInterface ownPresence = ConnectionStack.this.session.getPresence();
			response.setPresence(ownPresence);
			response.setType(ownPresence == PresenceValue.OFFLINE ? Constants.UNAVAILABLE : Constants.AVAILABLE);

			sendStanzaToStreamProcessor(response);
		} else
			contact.presenceProperty().setValue(presence.getPresence());
	}

	private void processIncomingIQ(IQInterface iq) {
		synchronized (this.searchIdLock) {
			if (this.lastSearchRequest == null || !this.lastSearchRequest.isResponseMatching(iq))
				return;
		}

		this.gui.provideSearchResults(iq.getDefaultContent());
	}

	@Override
	public void destroy() {
		this.networkStack.disconnect();
		this.controller.closeConnectionStack(this);
		this.gui.unregisterObserver(this.guiObserver);
	}

	private void processConnectionLoss(ConnectionLostReason reason) {
		logger.trace("Connection was terminated due to " + reason);
		if (!this.session.isDisconnecting() || !this.isDisconnected) {
			logger.trace("Notify GUI about unexpected connection termination");
			this.gui.onConnectionDied(getAccountConfiguration());
		} else {
			logger.trace("Notify GUI about expected connection termination");
			GuiController.getInstance().onDisconnected();
		}
		this.destroy();
	}

	private static class ConnectionIdentification implements NetworkConnectionIdentificationInterface {

		private String address;
		private int    port;

		private ConnectionIdentification(String address, int port) {
			this.address = address;
			this.port = port;
		}

		@Override
		public String getAddress() {
			return this.address;
		}

		@Override
		public int getPort() {
			return this.port;
		}
	}
}
