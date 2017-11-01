package edu.kit.ss17.chatsys.team1.shared.Network.NetworkDataProcessor;

import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainDataProcessor;
import org.jetbrains.annotations.Contract;

/**
 * Encapsulates a @code{NetworkPluginInterface} implementation so knowledge of the @code{ChainDataProcessor} class is not required for a plugin developer.
 */
public class NetworkDataProcessor extends ChainDataProcessor<byte[], byte[]> {

	private final NetworkPluginInterface plugin;

	/**
	 * Constructor to create a new @code{ChainDataProcessor} for the given plugin
	 *
	 * @param plugin the plugin to be encapsulated
	 */
	public NetworkDataProcessor(NetworkPluginInterface plugin) {
		this.plugin = plugin;
		// Bind Listener to the enabledProperty to remove this plugin from the Chain when it gets disabled
		this.plugin.getEnabledProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue && !newValue) {
				ChainDataProcessor.link(this.getLower(), this.getUpper());
				ChainDataProcessor.link(this,this);
			}
		});
	}

	@Contract(pure = true)
	private NetworkPluginInterface getPlugin() {
		return this.plugin;
	}

	protected byte[] processDataFromUpper(byte[] data) {
		return getPlugin().dataFromUpperReceived(data);
	}

	protected byte[] processDataFromLower(byte[] data) {
		return getPlugin().dataFromLowerReceived(data);
	}
}
