package edu.kit.ss17.chatsys.team1.shared.Network.NetworkDataProcessor;

import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkProtocolPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainDataDelegator;

/**
 * Encapsulates a @code{NetworkProtocolPluginInterface} implementation so knowledge of the @code{ChainDataProcessor} class is not required for a plugin developer.
 * <p>
 * NetworkProtocolDataProcessor is always at the lower end of a @code{NetworkDataProcessor} chain that is contained inside a @code{NetworkStack} instance.
 */
public class NetworkProtocolDataProcessor extends ChainDataDelegator<byte[]> {

	private NetworkProtocolPluginInterface plugin;

	public NetworkProtocolDataProcessor(NetworkProtocolPluginInterface plugin) {
		this.plugin = plugin;
		this.plugin.registerObserver(this::pushDataUp);
	}

	protected NetworkProtocolPluginInterface getPlugin() {
		return this.plugin;
	}

	@Override
	public final void pushDataDown(byte[] data) {
		getPlugin().sendData(processDataFromUpper(data));
	}
}
