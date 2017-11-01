package edu.kit.ss17.chatsys.team1.plugins.TCPConnectionPlugin;

import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkConnectionIdentificationInterface;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkConnectionOpenedInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 *
 */
public class ListenerPluginTest {

	private static final int PORT = 5222;
	private ListenerPlugin plugin;

	@Before
	public void setUp() {
		this.plugin = new ListenerPlugin(null);
	}

	@After
	public void tearDown() {
		this.plugin.stopListening();
	}

	@Test
	public void testRegisterObserver() {
		assertThat("No observers expected", this.plugin.getObservers().size(), is(0));
		this.plugin.registerObserver(new Observer());
		assertThat("An observer has to exist", this.plugin.getObservers().size(), is(1));
	}

	@Test
	public void testRegisterObserverImmutable() {
		NetworkConnectionOpenedInterface obs = new Observer();

		this.plugin.registerObserver(obs);
		int size = this.plugin.getObservers().size();
		this.plugin.registerObserver(obs);

		assertThat("Multiple observer registrations do not change observer count", this.plugin.getObservers().size(), is(size));
	}

	@Test
	public void testUnregisterObserver() {
		NetworkConnectionOpenedInterface obs = new Observer();
		this.plugin.registerObserver(obs);

		int size = this.plugin.getObservers().size();
		this.plugin.unregisterObserver(obs);

		assertThat("Un-registering observer reduces observer count", this.plugin.getObservers().size(), is(size - 1));
	}

	@Test
	public void testUnregisterObserverUnregistered() {
		NetworkConnectionOpenedInterface obs  = new Observer();
		NetworkConnectionOpenedInterface obs2 = new Observer();

		this.plugin.registerObserver(obs);
		assertThat("Observer required", this.plugin.getObservers().size(), is(1));

		this.plugin.unregisterObserver(obs2);
		assertThat("Un-registration of non-registered observer must not change observer count", this.plugin.getObservers().size(), is(1));
	}

	@Test
	public void testRegisterErrorObserver() {
		assertThat("No error observers expected", this.plugin.getErrorObservers().size(), is(0));
		this.plugin.registerErrorObserver(Mockito.mock(ProtocolErrorObserverInterface.class));
		assertThat("An error observer has to exist", this.plugin.getErrorObservers().size(), is(1));
	}

	@Test
	public void testRegisterErrorObserverImmutable() {
		ProtocolErrorObserverInterface obs = Mockito.mock(ProtocolErrorObserverInterface.class);

		this.plugin.registerErrorObserver(obs);
		int size = this.plugin.getErrorObservers().size();
		this.plugin.registerErrorObserver(obs);

		assertThat("Multiple error observer registrations do not change observer count", this.plugin.getErrorObservers().size(), is(size));
	}

	@Test
	public void testUnregisterErrorObserver() {
		ProtocolErrorObserverInterface obs = Mockito.mock(ProtocolErrorObserverInterface.class);
		this.plugin.registerErrorObserver(obs);

		int size = this.plugin.getErrorObservers().size();
		this.plugin.unregisterErrorObserver(obs);

		assertThat("Un-registering error observer reduces observer count", this.plugin.getErrorObservers().size(), is(size - 1));
	}

	@Test
	public void testUnregisterErrorObserverUnregistered() {
		ProtocolErrorObserverInterface obs  = Mockito.mock(ProtocolErrorObserverInterface.class);
		ProtocolErrorObserverInterface obs2 = Mockito.mock(ProtocolErrorObserverInterface.class);
		this.plugin.registerErrorObserver(obs);
		assertThat("Error observer required", this.plugin.getErrorObservers().size(), is(1));

		this.plugin.unregisterErrorObserver(obs2);
		assertThat("Un-registration of non-registered error observer must not change observer count", this.plugin.getErrorObservers().size(), is(1));
	}

	@Test
	public void testIsEnabled() {
		assertThat("Plugin has to be enabled per default", this.plugin.getEnabledProperty().getValue(), is(true));
	}


	@Test
	public void testGetWeight() {
		assertThat("Plugin must not have a weight different than zero", this.plugin.getWeight(), is(0));
	}

	@Test
	public void testGetPluginSet() {
		assertThat("Initial pluginset is zero", this.plugin.getPluginSet(), nullValue());
	}

	@Test
	public void testSetPluginSet() {
		PluginSet set = new PluginSet();
		this.plugin.setPluginSet(set);
		assertThat("Pluginset has to resemble set value", this.plugin.getPluginSet(), is(set));
	}

	@SuppressWarnings("BusyWait")
	@Test
	public void testStartListening() {
		assertThat("Plugin must not listen initially", this.plugin.isListening(), is(false));
		this.plugin.startListening();

		long start = System.currentTimeMillis();
		while (!this.plugin.isListening() && System.currentTimeMillis() - start <= ListenerPlugin.CYCLE_MS)
			try {
				sleep(10);
			} catch (InterruptedException ignored) {
			}

		assertThat("Call to startListening changes listening flag value", this.plugin.isListening(), is(true));
	}

	@SuppressWarnings("BusyWait")
	@Test
	public void testIsListening() {
		Observer obs = new Observer();
		this.plugin.registerObserver(obs);
		this.plugin.startListening();

		try (Socket sock = new Socket()) {
			SocketAddress target = new InetSocketAddress("localhost", PORT);
			sock.connect(target);
		} catch (IOException ignored) {
			fail("An IOException occured " + ignored.getMessage());
		}

		Box<Boolean> result = new Box<>(false);
		Thread t = new Thread(() -> {
			while (obs.getId() == null) {
				try {
					sleep(10);
				} catch (InterruptedException ignored) {
					return;
				}
			}
			result.setElement(true);
		});
		t.start();
		try {
			t.join(1000);
		} catch (InterruptedException ignored) {
			t.interrupt();
			fail("Not notified in time");
		}

		assertThat("Observer has to be notified", result.getElement(), is(true));
	}

	@Test(expected = CloneNotSupportedException.class)
	public void testClone() throws CloneNotSupportedException {
		this.plugin.clone();
	}

	private static class Observer implements NetworkConnectionOpenedInterface {

		private NetworkConnectionIdentificationInterface id;

		Observer() {
			this.id = null;
		}

		NetworkConnectionIdentificationInterface getId() {
			return this.id;
		}

		@Override
		public void connectionOpened(NetworkConnectionIdentificationInterface networkConnectionIdentification) {
			this.id = networkConnectionIdentification;
		}
	}
}
