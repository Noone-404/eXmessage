package edu.kit.ss17.chatsys.team1.plugins.TCPConnectionPlugin;

import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkConnectionOpenedInterface;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkProtocolListenerPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 * Listens server-side for incoming TCP connections.
 */
public class ListenerPlugin implements NetworkProtocolListenerPluginInterface {

	public static final int PORT     = 5222;
	static final        int CYCLE_MS = 1000;

	private static final Logger logger = LogManager.getLogger(APP_NAME);
	private final Thread                                       listenerThread;
	private       Collection<NetworkConnectionOpenedInterface> observers;
	private       Collection<ProtocolErrorObserverInterface>   errorObservers;
	private       PluginSetInterface                           pluginSet;

	private          boolean      listenerActive;
	private volatile ServerSocket socket;
	private BooleanProperty enabled = new SimpleBooleanProperty(true);

	ListenerPlugin(PluginSet pluginSet) {
		this.pluginSet = pluginSet;
		this.observers = new CopyOnWriteArrayList<>();
		this.errorObservers = new CopyOnWriteArrayList<>();

		this.listenerThread = new Thread(this::runSocketListener, "TCP ListenerPlugin SocketListener Thread");
	}

	@Override
	public void registerObserver(NetworkConnectionOpenedInterface observer) {
		if (!this.observers.contains(observer))
			this.observers.add(observer);
	}

	@Override
	public void unregisterObserver(NetworkConnectionOpenedInterface observer) {
		if (this.observers.contains(observer))
			this.observers.remove(observer);
	}

	@Override
	public void registerErrorObserver(ProtocolErrorObserverInterface observer) {
		if (!this.errorObservers.contains(observer))
			this.errorObservers.add(observer);
	}

	@Override
	public void unregisterErrorObserver(ProtocolErrorObserverInterface observer) {
		if (this.errorObservers.contains(observer))
			this.errorObservers.remove(observer);
	}


	@Override
	public BooleanProperty getEnabledProperty() {
		return this.enabled;
	}

	@Override
	public int getWeight() {
		return 0;
	}

	@Override
	public PluginSetInterface getPluginSet() {
		return this.pluginSet;
	}

	@Override
	public void setPluginSet(PluginSetInterface pluginSet) {
		this.pluginSet = pluginSet;
	}

	@Override
	public void startListening() {
		logger.info("Start listening on TCP/" + PORT);
		this.listenerThread.start();
	}

	@Override
	public boolean isListening() {
		return this.socket != null && !this.socket.isClosed();
	}

	void stopListening() {
		if (!isListening())
			return;

		this.listenerActive = false;
		try {
			this.socket.close();
		} catch (IOException ignored) {
		}

		try {
			this.listenerThread.join(CYCLE_MS * 2);
		} catch (InterruptedException ignored) {
		}
	}

	public final ListenerPlugin clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	Collection<NetworkConnectionOpenedInterface> getObservers() {
		return new ArrayList<>(this.observers);
	}

	Collection<ProtocolErrorObserverInterface> getErrorObservers() {
		return new ArrayList<>(this.errorObservers);
	}

	private void notifyObservers(SocketConnectionIdentification address) {
		this.observers.forEach(networkConnectionOpenedInterface -> networkConnectionOpenedInterface.connectionOpened(address));
	}

	private void notifyErrorObservers(ProtocolErrorInterface error) {
		this.errorObservers.forEach(protocolErrorObserverInterface -> protocolErrorObserverInterface.onProtocolError(error));
	}

	private void runSocketListener() {
		this.listenerActive = true;

		try (ServerSocket server = new ServerSocket(PORT)) {
			server.setSoTimeout(CYCLE_MS); // check for change of activity flag every second

			this.socket = server;

			while (this.listenerActive && isListening())
				try {
					notifyObservers(new SocketConnectionIdentification(server.accept()));
				} catch (SocketTimeoutException | SocketException ignored) {
					// TimeoutException: regularly recheck the flag
					// SocketException: occurs if socket was closed
				}

		} catch (IOException e) {
			//NetworkProtocolError error = new NetworkProtocolError(e.getMessage(), false); TODO probably remove this
			//notifyErrorObservers(error);
		} finally {
			this.listenerActive = false;
		}
	}
}
