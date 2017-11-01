package edu.kit.ss17.chatsys.team1.shared.StreamProcessor;

import edu.kit.ss17.chatsys.team1.shared.ConnectionStack.ConnectionStackBaseInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.Session.SessionBaseInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StreamFooterInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization.DeserializationManagerInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Plugins.DataElementPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Plugins.DocumentPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainDataProcessor;
import org.dom4j.Document;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class organizes the translation of Stanza-objects to XML-Documents and vice versa.
 */
public abstract class AbstractStreamProcessor extends ChainDataProcessor<Document, DataElementInterface> implements StreamProcessorInterface {

	private final Collection<DataElementPluginInterface> dataElementPlugins = new ArrayList<>();
	private final Collection<DocumentPluginInterface>    documentPlugins    = new ArrayList<>();
	protected DeserializationManagerInterface deserializationManager;
	private   SessionBaseInterface            sessionBase;
	private   ConnectionStackBaseInterface    connectionStackBase;
	private final Collection<ProtocolErrorObserverInterface> protocolErrorObservers = new ArrayList<>();

	private static Document serializeDataElement(DataElementInterface element) {
		return element.serialize();
	}

	@Override
	public void tryRegisterPlugin(PluginInterface plugin, boolean lastPlugin) {
		if (plugin instanceof DataElementPluginInterface) {
			if (plugin.getEnabledProperty().getValue()) {
				this.dataElementPlugins.add((DataElementPluginInterface) plugin);
			}

			plugin.getEnabledProperty().addListener((observable, oldValue, newValue) -> {
				if (oldValue && !newValue) {
					this.dataElementPlugins.remove(plugin);
				} else if (!oldValue && newValue) {
					this.dataElementPlugins.add((DataElementPluginInterface) plugin);
				}
			});
		} else if (plugin instanceof DocumentPluginInterface) {
			if (plugin.getEnabledProperty().getValue()) {
				this.documentPlugins.add((DocumentPluginInterface) plugin);
			}
			plugin.getEnabledProperty().addListener((observable, oldValue, newValue) -> {
				if (oldValue && !newValue) {
					this.documentPlugins.remove(plugin);
				} else if (!oldValue && newValue) {
					this.documentPlugins.add((DocumentPluginInterface) plugin);
				}
			});
		}
	}

	private DataElementInterface deserializeXML(Document object) {
		return this.deserializationManager.deserializeXML(object);
	}

	@Override
	protected Document processDataFromUpper(DataElementInterface data) {
		return serializeDataElement(data);
		//Plugin stuff happens in pushDataDown. (also the multiplexing)
	}

	@Override
	public void pushDataDown(DataElementInterface data) {

		// Pass incoming DataElements to all registered and enabled DataElementPlugins.
		// They're able to reject DataElements and to create new ones.
		ArrayList<DataElementInterface> elements = new ArrayList<>();
		elements.add(data);

		for (final DataElementPluginInterface plugin : this.dataElementPlugins) {
			for (int i = 0; i < elements.size(); i++) {
				ArrayList<DataElementInterface> result = new ArrayList<>(plugin.incomingDataElement(elements.get(i)));

				if (result.isEmpty()) {
					elements.remove(i);
					i--;
				} else {
					elements.remove(i);
					elements.addAll(i, result);

					// Don't send the plugins results back to the plugin.
					i += result.size() - 1;
				}
			}
		}

		for (final DataElementInterface element : elements) {
			Document processedData = processDataFromUpper(element); // <-- processing happens here
			if (processedData != null) {

				// Pass outgoing Documents to all registered and enabled DocumentPlugins.
				// They're able to reject Documents and to create new ones.
				ArrayList<Document> documents = new ArrayList<>();
				documents.add(processedData);

				for (final DocumentPluginInterface plugin : this.documentPlugins) {
					for (int i = 0; i < documents.size(); i++) {
						ArrayList<Document> result = new ArrayList<>(plugin.outgoingDocument(documents.get(i)));

						if (result.isEmpty()) {
							documents.remove(i);
							i--;
						} else {
							documents.remove(i);
							documents.addAll(i, result);

							// Don't send the plugins results back to the plugin.
							i += result.size() - 1;
						}
					}
				}

				for (final Document document : documents)
					this.getLower().pushDataDown(processedData);
			}
		}
	}

	@Override
	public void pushDataUp(Document data) {

		// Pass incoming Documents to all registered and enabled documentPlugins.
		// They're able to reject Documents and to create new ones.
		ArrayList<Document> elements = new ArrayList<>();
		elements.add(data);

		for (final DocumentPluginInterface plugin : this.documentPlugins) {
			for (int i = 0; i < elements.size(); i++) {
				ArrayList<Document> result = new ArrayList<>(plugin.incomingDocument(elements.get(i)));

				if (result.isEmpty()) {
					elements.remove(i);
					i--;
				} else {
					elements.remove(i);
					elements.addAll(i, result);

					// Don't send the plugins results back to the plugin.
					i += result.size() - 1;
				}
			}
		}

		for (final Document element : elements) {
			DataElementInterface processedData = processDataFromLower(element); // <-- processing happens here
			if (processedData != null) {

				// Pass outgoing DataElements to all registered and enabled DataElementPlugins.
				// They're able to reject DataElements and to create new ones.
				ArrayList<DataElementInterface> dataElements = new ArrayList<>();
				dataElements.add(processedData);

				for (final DataElementPluginInterface plugin : this.dataElementPlugins) {
					for (int i = 0; i < dataElements.size(); i++) {
						ArrayList<DataElementInterface> result = new ArrayList<>(plugin.outgoingDataElement(dataElements.get(i)));

						if (result.isEmpty()) {
							dataElements.remove(i);
							i--;
						} else {
							dataElements.remove(i);
							dataElements.addAll(i, result);

							// Don't send the plugins results back to the plugin.
							i += result.size() - 1;
						}
					}
				}

				for (final DataElementInterface dataElement : dataElements)
					this.getUpper().pushDataUp(dataElement);
			}
		}
	}

	@Override
	protected DataElementInterface processDataFromLower(Document data) {
		DataElementInterface deserialized = deserializeXML(data);

		if (!this.sessionBase.isNegotiationFinished())
			this.setUpper(this.sessionBase);

		else
			this.setUpper(this.connectionStackBase);

		return deserialized;
	}

	@Override
	public void registerErrorObserver(ProtocolErrorObserverInterface observer) {
		this.protocolErrorObservers.add(observer);
		for (final PluginInterface plugin : this.dataElementPlugins) {
			plugin.registerErrorObserver(observer);
		}
		for (final PluginInterface plugin : this.documentPlugins) {
			plugin.registerErrorObserver(observer);
		}
	}

	@Override
	public void unregisterErrorObserver(ProtocolErrorObserverInterface observer) {
		this.protocolErrorObservers.remove(observer);
		for (final PluginInterface plugin : this.dataElementPlugins) {
			plugin.unregisterErrorObserver(observer);
		}
		for (final PluginInterface plugin : this.documentPlugins) {
			plugin.unregisterErrorObserver(observer);
		}
	}

	@Override
	public void notifyErrorObservers(ProtocolErrorInterface error) {
		for (final ProtocolErrorObserverInterface observer : this.protocolErrorObservers) {
			observer.onProtocolError(error);
		}
	}

	@Override
	public void setSession(SessionBaseInterface session) {
		this.sessionBase = session;
	}

	@Override
	public void setConnectionStackBase(ConnectionStackBaseInterface connectionStackBase) {
		this.connectionStackBase = connectionStackBase;
	}
}
