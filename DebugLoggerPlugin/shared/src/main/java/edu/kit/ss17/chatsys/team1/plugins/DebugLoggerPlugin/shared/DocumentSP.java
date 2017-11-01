package edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared;

import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Plugins.DocumentPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObservableInterface;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static edu.kit.ss17.chatsys.team1.shared.Constants.PLUGIN_LOGGERS;

/**
 * Document Plugin which gets registered at the Stream Processor to debug incoming and outgoing Documents.
 */
public class DocumentSP implements DocumentPluginInterface, ObservableInterface<DocumentObserverInterface> {

	private static final Collection<DocumentObserverInterface> observers = new ArrayList<>();
	private static final Logger                                logger    = LogManager.getLogger(PLUGIN_LOGGERS);

	private final PluginSetInterface pluginSet;
	private BooleanProperty enabled = new SimpleBooleanProperty(false);

	/**
	 * Constructor of the DocumentSP class.
	 *
	 * @param pluginSet the PluginSet this plugin belongs to.
	 */
	public DocumentSP(PluginSetInterface pluginSet) {
		this.pluginSet = pluginSet;
	}

	@Override
	public Collection<Document> incomingDocument(Document data) { // incoming means coming INTO stream processor = data is being send to client
		for (DocumentObserverInterface observer : observers)
			observer.sawIncomingDocument(data);

		logger.info(OutputFormatter.render("(X)-->StreamProcessor: received XML document", data.getRootElement().asXML(), true, false));
		return Collections.singletonList(data);
	}

	@Override
	public Collection<Document> outgoingDocument(Document data) { // outgoing means LEAVING stream processor = data was sent by client
		for (DocumentObserverInterface observer : observers)
			observer.sawLeavingDocument(data);

		logger.info(OutputFormatter.render("StreamProcessor-->(X'): sending XML document", data.getRootElement().asXML(), true, true));
		return Collections.singletonList(data);
	}

	@Override
	public BooleanProperty getEnabledProperty() {
		return this.enabled;
	}

	@Override
	public int getWeight() {
		return 0;
	}

	@Override
	public PluginSetInterface getPluginSet() {
		return this.pluginSet;
	}

	@Override
	public void setPluginSet(PluginSetInterface pluginSet) {
		// Do nothing
	}

	@Override
	public void registerErrorObserver(ProtocolErrorObserverInterface observer) {
		// Do nothing
	}

	@Override
	public void unregisterErrorObserver(ProtocolErrorObserverInterface observer) {
		// Do nothing
	}

	@Override
	public void registerObserver(DocumentObserverInterface observer) {
		if (!observers.contains(observer))
			observers.add(observer);
	}

	@Override
	public void unregisterObserver(DocumentObserverInterface observer) {
		if (observers.contains(observer))
			observers.remove(observer);
	}
}
