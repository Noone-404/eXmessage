package edu.kit.ss17.chatsys.team1.plugins.TCPConnectionPlugin;

import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkConnectionIdentificationInterface;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkProtocolPluginInterface.NetworkProtocolPluginDataReceivedInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 *
 */
public class ProtocolPluginTest {

	private static final int PORT = 5222;
	private ProtocolPlugin plugin;

	@Before
	public void setUp() {
		this.plugin = new ProtocolPlugin(null);
	}

	@After
	public void tearDown() {
		this.plugin.disconnect();
	}

	@Test
	public void testConnect() {
		Box<Boolean> received = new Box<>(false);

		Thread listener = new Thread(() -> {
			try (ServerSocket server = new ServerSocket(PORT)) {
				try (Socket ignored = server.accept()) {
					received.setElement(true);
				}
			} catch (IOException e) {
				fail("An IOException occurred: ServerSocket@connect: " + e.getMessage());
			}
		});

		listener.start();

		SocketConnectionIdentificationInterface scii = null;
		try {
			scii = new SocketConnectionIdentification(new Socket("localhost", PORT));
		} catch (IOException e) {
			fail(e.getMessage());
		}

		try {
			this.plugin.connect(scii);
		} catch (IOException e) {
			fail("Failed to connect: " + e.getMessage());
		}

		try {
			listener.join(ProtocolPlugin.CYCLE_MS);
		} catch (InterruptedException ignored) {
			fail("New connection not received in time");
		}
		assertThat("An socket has to be received", received.getElement(), is(true));

		long start = System.currentTimeMillis();
		while (!this.plugin.isConnected() && System.currentTimeMillis() - start <= ProtocolPlugin.CYCLE_MS)
			try {
				sleep(10);
			} catch (InterruptedException ignored) {
			}

		assertThat("Socket has to be connected", this.plugin.isConnected(), is(true));
	}

	@Test
	public void testSendData() {
		byte[]             data    = {0x6B, 0x5A};
		final Box<Boolean> success = new Box<>(false);

		Box<Boolean> socketOpen = new Box<>(false);
		Thread listener = new Thread(() -> {
			try (ServerSocket server = new ServerSocket(PORT)) {
				socketOpen.setElement(true);
				try (Socket destination = server.accept()) {
					byte[] buffer = new byte[255];
					int    length;

					try (InputStream is = destination.getInputStream()) {
						length = is.read(buffer);
					}

					assertThat("Two bytes expected", length, is(2));
					buffer = Arrays.copyOf(buffer, length);

					assertThat("First byte has to equal source", data[0], is(buffer[0]));
					assertThat("Second byte has to equal source", data[1], is(buffer[1]));

					success.setElement(true);
				}
			} catch (IOException ignored) {
				fail("An IOException occurred: ServerSocket@sendData");
			}
		});
		listener.start();

		NetworkConnectionIdentificationInterface id = null;
		while (!socketOpen.getElement())
			;
		try {
			id = new SocketConnectionIdentification(new Socket("localhost", PORT));
		} catch (IOException ignored) {
			fail("Failed to create socket " + ignored.getMessage());
		}

		try {
			this.plugin.connect(id);
		} catch (IOException e) {
			fail("Failed to connect: " + e.getMessage());
		}

		this.plugin.sendData(data);

		try {
			listener.join(ProtocolPlugin.CYCLE_MS);
		} catch (InterruptedException ignored) {
		}

		assertThat("Received data in time", success.getElement(), is(true));
	}

	@Test
	public void testIsEnabled() {
		assertThat("Protocol plugin has to be enabled per default", this.plugin.getEnabledProperty().getValue(), is(true));
	}

	@Test
	public void testGetWeight() {
		assertThat("Weight has to be zero", this.plugin.getWeight(), is(0));
	}

	@Test
	public void testGetPluginSet() {
		assertThat("Initial plugin set is expected to be null", this.plugin.getPluginSet(), nullValue());
	}

	@Test
	public void testSetPluginSet() {
		PluginSet set = new PluginSet();
		this.plugin.setPluginSet(set);

		assertThat("Pluginset has to resemble changed value", this.plugin.getPluginSet(), is(set));
	}


	@Test
	public void testRegisterObserver() {
		assertThat("No event listeners expected", this.plugin.getEventListeners().size(), is(0));
		this.plugin.registerObserver(Mockito.mock(NetworkProtocolPluginDataReceivedInterface.class));
		assertThat("An event listener has to exist", this.plugin.getEventListeners().size(), is(1));
	}

	@Test
	public void testRegisterObserverImmutable() {
		NetworkProtocolPluginDataReceivedInterface obs = Mockito.mock(NetworkProtocolPluginDataReceivedInterface.class);

		this.plugin.registerObserver(obs);
		int size = this.plugin.getEventListeners().size();
		this.plugin.registerObserver(obs);

		assertThat("Multiple event listener registrations do not change listener count", this.plugin.getEventListeners().size(), is(size));
	}

	@Test
	public void testUnregisterObserver() {
		NetworkProtocolPluginDataReceivedInterface obs = Mockito.mock(NetworkProtocolPluginDataReceivedInterface.class);
		this.plugin.registerObserver(obs);

		int size = this.plugin.getEventListeners().size();
		this.plugin.unregisterObserver(obs);

		assertThat("Un-registering event listener reduces listener count", this.plugin.getEventListeners().size(), is(size - 1));
	}

	@Test
	public void testUnregisterObserverUnregistered() {
		NetworkProtocolPluginDataReceivedInterface obs  = Mockito.mock(NetworkProtocolPluginDataReceivedInterface.class);
		NetworkProtocolPluginDataReceivedInterface obs2 = Mockito.mock(NetworkProtocolPluginDataReceivedInterface.class);

		this.plugin.registerObserver(obs);
		assertThat("Event listener required", this.plugin.getEventListeners().size(), is(1));

		this.plugin.unregisterObserver(obs2);
		assertThat("Un-registration of non-registered event listener must not change observer count", this.plugin.getEventListeners().size(), is(1));
	}


	@Test
	public void testRegisterErrorObserver() {
		assertThat("No error observers expected (protocol plugin)", this.plugin.getErrorObservers().size(), is(0));
		this.plugin.registerErrorObserver(Mockito.mock(ProtocolErrorObserverInterface.class));
		assertThat("An error observer has to exist (protocol plugin)", this.plugin.getErrorObservers().size(), is(1));
	}

	@Test
	public void testRegisterErrorObserverImmutable() {
		ProtocolErrorObserverInterface obs = Mockito.mock(ProtocolErrorObserverInterface.class);

		this.plugin.registerErrorObserver(obs);
		int size = this.plugin.getErrorObservers().size();
		this.plugin.registerErrorObserver(obs);

		assertThat("Multiple error observer registrations do not change observer count (protocol plugin)", this.plugin.getErrorObservers().size(), is(size));
	}

	@Test
	public void testUnregisterErrorObserver() {
		ProtocolErrorObserverInterface obs = Mockito.mock(ProtocolErrorObserverInterface.class);
		this.plugin.registerErrorObserver(obs);

		int size = this.plugin.getErrorObservers().size();
		this.plugin.unregisterErrorObserver(obs);

		assertThat("Un-registering error observer reduces observer count (protocol plugin)", this.plugin.getErrorObservers().size(), is(size - 1));
	}

	@Test
	public void testUnregisterErrorObserverUnregistered() {
		ProtocolErrorObserverInterface obs  = Mockito.mock(ProtocolErrorObserverInterface.class);
		ProtocolErrorObserverInterface obs2 = Mockito.mock(ProtocolErrorObserverInterface.class);
		this.plugin.registerErrorObserver(obs);
		assertThat("Error observer required (protocol plugin)", this.plugin.getErrorObservers().size(), is(1));

		this.plugin.unregisterErrorObserver(obs2);
		assertThat("Un-registration of non-registered error observer must not change observer count (protocol plugin)", this.plugin.getErrorObservers().size(), is(1));
	}

	@Test
	public void testClone() {
		ProtocolPlugin clone = this.plugin.clone();
		assertThat("Cloned plugin must not be null", clone, notNullValue());
	}
}
