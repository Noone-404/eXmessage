package edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement;

import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkProtocolListenerPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of @code{NetworkFactoryInterface}.
 */
public class NetworkStackFactory implements NetworkStackFactoryInterface {

	private static NetworkStackFactory                        instance;
	private        NetworkProtocolListenerPluginInterface     networkProtocolListenerPlugin;
	private        List<NetworkStackFactoryObserverInterface> observer;

	private NetworkStackFactory() {
		this.observer = new ArrayList<>();
	}

	public static NetworkStackFactory getInstance() {
		return (instance != null) ? instance : (instance = new NetworkStackFactory());
	}

	public static NetworkStackInterface make() {
		return getInstance().makeInstance();
	}

	@Override
	public NetworkStackInterface makeInstance() {
		return new NetworkStack();
	}

	@Override
	public void registerObserver(NetworkStackFactoryObserverInterface observer) {
		if (!this.observer.contains(observer))
			this.observer.add(observer);
	}

	@Override
	public void unregisterObserver(NetworkStackFactoryObserverInterface observer) {
		if (this.observer.contains(observer))
			this.observer.remove(observer);
	}

	/**
	 * Notifies event listeners about a new connection.
	 *
	 * @param stack the new @code{ConnectionStackInterface} implementation
	 */
	private void notifyNewConnection(NetworkStackInterface stack) {
		this.observer.forEach(networkStackFactoryObserver -> networkStackFactoryObserver.receivedNewConnection(stack));
	}

	@Override
	public void tryRegisterPlugin(PluginInterface plugin, boolean lastPlugin) {
		if (plugin instanceof NetworkProtocolListenerPluginInterface) {

			if (networkProtocolListenerPlugin == null) {
				networkProtocolListenerPlugin = (NetworkProtocolListenerPluginInterface) plugin;

				networkProtocolListenerPlugin.registerObserver(networkConnectionIdentification -> {
					NetworkStackInterface newStack = new NetworkStack();
					newStack.setConnectionInfo(networkConnectionIdentification);
					notifyNewConnection(newStack);
				});
			}

		}
	}

	@Override
	public void startListening() throws IllegalStateException {
		if (this.networkProtocolListenerPlugin == null)
			throw new IllegalStateException("Cannot start listening - no listener plugin registered!");

		if (this.networkProtocolListenerPlugin.isListening())
			throw new IllegalStateException("Already listening!");

		this.networkProtocolListenerPlugin.startListening();
	}
}
