package edu.kit.ss17.chatsys.team1.server.Controller;

import edu.kit.ss17.chatsys.team1.server.Router.RouterInterface;
import edu.kit.ss17.chatsys.team1.server.Session.SessionFactory;
import edu.kit.ss17.chatsys.team1.server.Session.SessionInterface;
import edu.kit.ss17.chatsys.team1.server.StreamProcessor.ServerStreamProcessorFactory;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement.NetworkStackInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginManagerFactory;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginManagerInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorManagerFactory;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorManagerInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolTranslator.ProtocolTranslatorFactory;
import edu.kit.ss17.chatsys.team1.shared.ProtocolTranslator.ProtocolTranslatorInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StanzaInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StreamFooterInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.StreamProcessorInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainDataProcessor;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainUpperDataProcessor;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;

import java.io.IOException;
import java.time.Instant;

/**
 * Our server connection stack.
 */
public class ConnectionStack extends ChainUpperDataProcessor<DataElementInterface> implements ConnectionStackInterface {

	private final ControllerInterface         controller;
	private final ErrorManagerInterface       errorManager;
	private final PluginManagerInterface      pluginManager;
	private final NetworkStackInterface       networkStack;
	private final ProtocolTranslatorInterface translator;
	private final StreamProcessorInterface    streamProcessor;
	private final SessionInterface            session;
	private       RouterInterface             router;

	public ConnectionStack(NetworkStackInterface networkStack) throws IOException {
		this.networkStack = networkStack;

		this.controller = Controller.getInstance();
		this.errorManager = ErrorManagerFactory.make();
		this.session = SessionFactory.make();
		this.session.setConnectionStack(this);
		this.translator = ProtocolTranslatorFactory.make();
		this.streamProcessor = ServerStreamProcessorFactory.make();
		this.streamProcessor.setSession(this.session);
		this.pluginManager = PluginManagerFactory.make();

		initPluginManager();
		initErrorManager();
		registerErrorObserver();
		initChain();
		this.networkStack.registerConnectionStateObserver(reason -> this.destroy());

		this.networkStack.connect();
	}

	/**
	 * Tell the components which one is lower and upper.
	 */
	private void initChain() {
		ChainDataProcessor.link(this.networkStack, this.translator);
		ChainDataProcessor.link(this.translator, this.streamProcessor);
		ChainDataProcessor.link(this.streamProcessor, this);
		streamProcessor.setConnectionStackBase(this); // so StreamProcessor can switch back and forth between different uppers Session and this

		this.session.setLower(this.streamProcessor);
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
	 * Registers an error observer at the error manager
	 */
	private void registerErrorObserver() {
		this.errorManager.registerObserver(error -> {
			if (error.isFatal())
				disconnect();
		});
	}

	private void disconnect() {
		if (this.session.isDisconnecting()) {
			// We've already took action before and are now allowed to close the connection.
			this.destroy();
		} else {
			// Tell the opponent we want to disconnect.
			this.session.terminateSession();
		}
	}

	@Override
	public JID getJID() {
		return this.session.getJid();
	}

	@Override
	public void setRouter(RouterInterface router) {
		this.router = router;
	}

	@Override
	public void sendStanzaToUser(StanzaInterface stanza) {
		this.streamProcessor.pushDataDown(stanza);
	}

	@Override
	public void sendStanzaToOther(StanzaInterface stanza) {
		this.router.sendStanza(stanza);
	}

	@Override
	public void negotiationFinished(boolean success) {
		if (success) {
			// Register at router.
			this.router.registerConnectionStack(this);
		} else {
			// Soft disconnect.
			this.disconnect();
		}
	}

	@Override
	public void destroy() {
		this.controller.closeConnectionStack(this);
		this.router.unregisterConnectionStack(this);
	}

	/**
	 * Called by @code{StreamProcessor} if new data has arrived.
	 *
	 * @param data the data that is handed over
	 */
	@Override
	public void pushDataUp(DataElementInterface data) {
		if (data instanceof StanzaInterface) {
			StanzaInterface stanza = (StanzaInterface) data;
			stanza.setServerReceiveDate(Instant.now());
			if (stanza.getReceiver().isEmpty())
				this.router.processStanza(stanza);
			else
				sendStanzaToOther(stanza);
		} else if (data instanceof StreamFooterInterface) {
			this.disconnect();
		}
	}
}
