package edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.server;

import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared.DataElementSP;
import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared.DocumentSP;
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

	private static final String NAME    = "Debug Logger - Server";
	private static final String DESC    = "Helps debugging the flow of data structures through various components.";
	private static final String VERSION = "1.0";
	private final Collection<PluginInterface> plugins;
	private final BooleanProperty             enabled;

	public PluginSet() {
		DataElementSP dataElementPlugin = new DataElementSP(this);
		DocumentSP    documentPlugin    = new DocumentSP(this);
		this.plugins = Arrays.asList(dataElementPlugin, documentPlugin);
		this.enabled = new SimpleBooleanProperty(false);
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
		for (PluginInterface plugin : this.plugins)
			plugin.getEnabledProperty().setValue(true);
	}

	@Override
	public final PluginSet clone() {
		return new PluginSet();
	}
}
