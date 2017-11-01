package edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement;

import edu.kit.ss17.chatsys.team1.shared.Network.NetworkDataProcessor.NetworkDataProcessor;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkDataProcessor.NetworkPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkDataProcessor.NetworkProtocolDataProcessor;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkConnectionIdentificationInterface;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkProtocolPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainDataDelegator;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainDataProcessor;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Default implementation of @code{NetworkStackInterface}.
 */
public class NetworkStack extends ChainDataDelegator<byte[]> implements NetworkStackInterface {

	private NetworkProtocolPluginInterface           protocolPlugin;
	private List<NetworkPluginInterface>             processorPluginList;
	private List<ProtocolErrorObserverInterface>     errorObservers;
	private NetworkConnectionIdentificationInterface connectionInfo;
	private boolean                                  registrationFinished;
	private String                                   protocolName;
	private Collection<ConnectionLostObserverInterface> connectionStateObservers = new ArrayList<>();

	NetworkStack() {
		this.processorPluginList = new ArrayList<>();
		this.errorObservers = new ArrayList<>();
	}

	@Override
	public void setConnectionInfo(NetworkConnectionIdentificationInterface connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

	@Override
	public void connect() throws IOException {
		if (this.protocolPlugin == null)
			throw new IllegalStateException("No protocol plugin registered");

		if (this.connectionInfo == null)
			throw new IllegalStateException("No connection info available");

		this.protocolPlugin.connect(this.connectionInfo);
	}

	@Override
	public void disconnect() {
		if (this.protocolPlugin == null)
			throw new IllegalStateException("No protocol plugin registered");

		this.protocolPlugin.disconnect();
	}

	@Override
	public void setProtocol(String protocolName) {
		this.protocolName = protocolName;
	}

	@Override
	public void tryRegisterPlugin(PluginInterface plugin, boolean lastPlugin) {
		if (this.registrationFinished)
			throw new IllegalStateException("Cannot register plugins after registration is finished");

		if (plugin instanceof NetworkProtocolPluginInterface)
			if (this.protocolName == null || this.protocolName.equals(plugin.getPluginSet().getName()))
				this.protocolPlugin = (NetworkProtocolPluginInterface) plugin;
			else if (plugin instanceof NetworkPluginInterface)
				if (this.protocolName == null || this.protocolName.equals(plugin.getPluginSet().getName()))
					this.processorPluginList.add((NetworkPluginInterface) plugin);

		if (lastPlugin)
			initializePlugins();
	}

	private void initializePlugins() {
		if (this.protocolPlugin == null)
			throw new IllegalStateException("Missing protocol plugin");

		this.connectionStateObservers.forEach((observer) -> this.protocolPlugin.registerConnectionStateObserver(observer));

		final class Box<T> { // Boxing needed to update final value

			private T element;

			private Box(T element) {
				this.setElement(element);
			}

			@Contract(pure = true)
			private T getElement() {
				return this.element;
			}

			private void setElement(T element) {
				this.element = element;
			}
		}

		// Box is final and allows to modify the contained element from a final context
		final Box<ChainDataProcessor<byte[], byte[]>> lastProcessorBox = new Box<>(new NetworkProtocolDataProcessor(this.protocolPlugin)); // first element of network stack

		this.processorPluginList.stream().sorted(Comparator.comparingInt(PluginInterface::getWeight)).forEachOrdered((NetworkPluginInterface networkPlugin) -> {
			networkPlugin.registerErrorObserver(this::protocolErrorOccurred);
			// Add Plugin to the Chain when they get enabled
			if (networkPlugin.getEnabledProperty().getValue()) {
				// create DataProcessor and link with previous Processor
				NetworkDataProcessor currentProcessor = new NetworkDataProcessor(networkPlugin);
				ChainDataProcessor.link(lastProcessorBox.getElement(), currentProcessor);

				// update reference of last processor
				lastProcessorBox.setElement(currentProcessor);
			}
			//Bind listener to the enabledProperty to add plugins at the end of the chain when they get enabled
			networkPlugin.getEnabledProperty().addListener((observable, oldValue, newValue) -> {
				if (!oldValue && newValue) {
					NetworkDataProcessor newProcessor = new NetworkDataProcessor(networkPlugin);
					ChainDataProcessor.link(newProcessor, this);
					ChainDataProcessor.link(this.getLower(), newProcessor);
				}
			});
		});

		// link the NetworkStack to the head of the NetworkProcessor chain
		ChainDataProcessor.link(lastProcessorBox.getElement(), this);

		this.registrationFinished = true;
	}

	/**
	 * Notifies observers about a protocol error
	 *
	 * @param error The error to notify about
	 */
	protected void protocolErrorOccurred(ProtocolErrorInterface error) {
		this.errorObservers.forEach(protocolErrorObserver -> protocolErrorObserver.onProtocolError(error));
	}

	@Override
	public void registerConnectionStateObserver(ConnectionLostObserverInterface observer) {
		if (!this.connectionStateObservers.contains(observer)) {
			this.connectionStateObservers.add(observer);
			if (this.protocolPlugin != null) {
				this.protocolPlugin.registerConnectionStateObserver(observer);
			}
		}
	}

	@Override
	public void unregisterConnectionStateObserver(ConnectionLostObserverInterface observer) {
		if(this.connectionStateObservers.contains(observer)) {
			this.connectionStateObservers.add(observer);
			if (this.protocolPlugin !=null) {
				this.protocolPlugin.unregisterConnectionStateObserver(observer);
			}
		}
	}
}
