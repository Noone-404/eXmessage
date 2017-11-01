package edu.kit.ss17.chatsys.team1.plugins.TCPConnectionPlugin;

import edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement.ConnectionLostObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement.ConnectionLostObserverInterface.ConnectionLostReason;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkConnectionIdentificationInterface;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkProtocolPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;


/**
 * Sends and receives messages via TCP/IP as part of a {@link NetworkInterface}.
 */
public class ProtocolPlugin implements NetworkProtocolPluginInterface {

	static final         int    CYCLE_MS    = 1000;
	private static final Logger logger      = LogManager.getLogger(APP_NAME);
	private static final int    BUFFER_SIZE = 0x4000;

	private final Collection<ProtocolErrorObserverInterface>             errorObservers;
	private final Collection<NetworkProtocolPluginDataReceivedInterface> eventListeners;
	private final Collection<ConnectionLostObserverInterface>            connectionLostObservers;
	private       PluginSetInterface                                     set;

	private Thread          listenerThread;
	private Socket          socket;
	private boolean         isListening;
	private BooleanProperty enabledProperty;
	private ExecutorService eventHandlerExecutor;
	private OutputStream    outputStream;

	ProtocolPlugin(PluginSetInterface pluginSet) {
		this.connectionLostObservers = new CopyOnWriteArrayList<>();
		this.errorObservers = new CopyOnWriteArrayList<>();
		this.eventListeners = new CopyOnWriteArrayList<>();
		this.set = pluginSet;
		this.eventHandlerExecutor = Executors.newSingleThreadExecutor(r -> {
			Thread t = new Thread(r, "TCP ProtocolPlugin EventHandler Executor");
			t.setDaemon(true);
			return t;
		});
		this.enabledProperty = new SimpleBooleanProperty(true);
	}

	@Override
	public void connect(NetworkConnectionIdentificationInterface connectionInfo) throws IOException {
		if (this.socket != null && this.socket.isConnected())
			throw new IllegalStateException("Already connected");

		try {
			this.socket = ((SocketConnectionIdentificationInterface) connectionInfo).getSocket();
		} catch (ClassCastException ignored) {
			this.socket = new Socket(connectionInfo.getAddress(), connectionInfo.getPort());
		}
		this.socket.setSoTimeout(CYCLE_MS);
		this.outputStream = new BufferedOutputStream(this.socket.getOutputStream());
		this.listenerThread = new Thread(this::runClientSocketListener, "TCP ProtocolPlugin SocketListener Thread");
		this.listenerThread.start();
	}

	boolean isConnected() {
		return this.socket != null && this.socket.isConnected() && !this.socket.isClosed();
	}

	@Override
	public void disconnect() {
		if (!isConnected())
			return;

		this.isListening = false;
		try {
			this.socket.close();
		} catch (IOException ignored) {
		}

		if (this.listenerThread != null)
			try {
				this.listenerThread.join(CYCLE_MS * 2);
			} catch (InterruptedException ignored) {
			}

		// shutdown eventHandlerExecutor last, so that any connectionLost-events may still be reported
		this.eventHandlerExecutor.shutdown(); // do not await execution in eventHandlerExecutor to prevent deadlocks; disconnect() might have been invoked by an event listener
	}

	@Override
	public void sendData(byte[] data) {
		if (!this.isConnected())
			throw new IllegalStateException("This plugin is not connected to a target address.");

		if (this.outputStream == null)
			throw new IllegalStateException("The output stream is not set.");

		try {
			this.outputStream.write(data);
			this.outputStream.flush();
			logger.trace("Sent " + data.length + " bytes of data");
		} catch (IOException e) {
			logger.error("Failed to send " + (data != null ? data.length : "[null]") + " bytes of data");
			//NetworkProtocolError error = new NetworkProtocolError(e.getMessage(), false); // TODO notify client
			//notifyErrorObservers(error);
		}
	}


	@Override
	public BooleanProperty getEnabledProperty() {
		return this.enabledProperty;
	}

	@Override
	public int getWeight() {
		return 0;
	}

	@Override
	public PluginSetInterface getPluginSet() {
		return this.set;
	}

	@Override
	public void setPluginSet(PluginSetInterface pluginSet) {
		this.set = pluginSet;
	}

	@Override
	public void registerObserver(NetworkProtocolPluginDataReceivedInterface observer) {
		if (!this.eventListeners.contains(observer))
			this.eventListeners.add(observer);
	}

	@Override
	public void unregisterObserver(NetworkProtocolPluginDataReceivedInterface observer) {
		if (this.eventListeners.contains(observer))
			this.eventListeners.remove(observer);
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
	public final ProtocolPlugin clone() {
		return new ProtocolPlugin(this.set);
	}

	Collection<NetworkProtocolPluginDataReceivedInterface> getEventListeners() {
		return new ArrayList<>(this.eventListeners);
	}

	Collection<ProtocolErrorObserverInterface> getErrorObservers() {
		return new ArrayList<>(this.errorObservers);
	}

	/**
	 * Notifies all listeners about new data.
	 *
	 * @param data the data to be notified about, usually data received by the socket
	 */
	private void notifyEventListeners(final byte[] data) {
		this.eventHandlerExecutor.execute(() -> this.eventListeners.forEach(listener -> listener.dataReceived(data)));
	}

	/**
	 * Notifies observers about a protocol error.
	 *
	 * @param error the error to notify about
	 */
	private void notifyErrorObservers(ProtocolErrorInterface error) { // TODO wenn das hier nie aufgerufen wird, brauchen wir die ErrorObservers überhaupt noch?
		this.eventHandlerExecutor.execute(() -> this.errorObservers.forEach(o -> o.onProtocolError(error)));
	}

	private void notifyConnectionLostObservers(final ConnectionLostReason reason) {
		this.eventHandlerExecutor.execute(() -> this.connectionLostObservers.forEach(o -> o.onConnectionLost(reason)));
	}

	private void runClientSocketListener() {
		if (!isConnected()) { // client might have closed the connection
			logger.debug("Exit listener thread because socket is not connected");
			notifyConnectionLostObservers(ConnectionLostReason.SOCKET_CLOSED);
			return;
		}
		this.isListening = true;

		try (InputStream input = new BufferedInputStream(this.socket.getInputStream())) {
			logger.trace("Start listening");

			byte[]  buffer   = new byte[BUFFER_SIZE];
			boolean notified = false;

			@SuppressWarnings("TooBroadScope") int length;
			while (this.isListening && isConnected())
				try {
					if ((length = input.read(buffer)) > 0) {
						logger.trace("Received " + length + " bytes of data");
						notifyEventListeners(Arrays.copyOf(buffer, length));
					} else if (length < 0) { // EOF
						logger.trace("EOF encountered");
						notifyConnectionLostObservers(ConnectionLostReason.EOF);
						notified = true;
						break;
					} else {
						logger.trace("No data received");
					}
				} catch (SocketTimeoutException ignored) {
					// recheck flags
				} catch (SocketException ignored) { // SocketException is thrown if socket is closed or connection is reset
					if (isConnected()) { // connection was reset, but socket is still open
						logger.trace("Connection was reset");
						notifyConnectionLostObservers(ConnectionLostReason.CONNECTION_RESET);
						notified = true;
						break; // close socket by closing input stream
					}
				}

			if (!notified) {
				logger.trace("Connection was closed");
				notifyConnectionLostObservers(ConnectionLostReason.SOCKET_CLOSED);
			}
			logger.trace("Closing socket");
		} catch (IOException e) {
			logger.error("Exit listener thread due to exception");
			e.printStackTrace();
			notifyConnectionLostObservers(ConnectionLostReason.ERROR);
		} finally {
			this.isListening = false;
		}
	}

	@Override
	public void registerConnectionStateObserver(ConnectionLostObserverInterface observer) {
		if (!this.connectionLostObservers.contains(observer)) {
			this.connectionLostObservers.add(observer);
		}
	}

	@Override
	public void unregisterConnectionStateObserver(ConnectionLostObserverInterface observer) {
		if (this.connectionLostObservers.contains(observer)) {
			this.connectionLostObservers.remove(observer);
		}
	}
}
