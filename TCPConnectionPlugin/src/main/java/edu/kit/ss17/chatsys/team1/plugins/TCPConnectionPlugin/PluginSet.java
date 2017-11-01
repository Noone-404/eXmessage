package edu.kit.ss17.chatsys.team1.plugins.TCPConnectionPlugin;

import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkProtocolListenerPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkProtocolPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Bundles @code{NetworkProtocolPluginInterface} and @code{NetworkProtocolListenerPluginInterface} implementations that enable the application to communicate via the TCP/IP
 * protocol suite.
 */
public class PluginSet implements PluginSetInterface {

	private static final String NAME    = "TCP";
	private static final String DESC    = "Provides basic functionality for sending and receiving messages using the TCP/IP protocol suite.";
	private static final String VERSION = "1.0";
	private final Collection<PluginInterface> plugins;
	private final BooleanProperty             enabled;

	public PluginSet() {
		@SuppressWarnings("ThisEscapedInObjectConstruction") NetworkProtocolPluginInterface         protocolPlugin = new ProtocolPlugin(this);
		@SuppressWarnings("ThisEscapedInObjectConstruction") NetworkProtocolListenerPluginInterface listenerPlugin = new ListenerPlugin(this);
		this.plugins = Arrays.asList(protocolPlugin, listenerPlugin);
		this.enabled = new SimpleBooleanProperty(true);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESC;
	}

	@Override
	public String getVersion() {
		return VERSION;
	}

	@Override
	public Collection<PluginInterface> getPlugins() {
		return Collections.unmodifiableCollection(this.plugins);
	}

	@Override
	public BooleanProperty getEnabledProperty() {
		return this.enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}

	@Override
	public void enablePlugins() {
	}

	@Override
	public final PluginSet clone() {
		return new PluginSet();
	}
}
