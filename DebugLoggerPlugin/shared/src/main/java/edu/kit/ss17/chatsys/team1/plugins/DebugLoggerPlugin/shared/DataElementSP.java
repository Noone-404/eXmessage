package edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared;

import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Plugins.DataElementPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObservableInterface;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static edu.kit.ss17.chatsys.team1.shared.Constants.PLUGIN_LOGGERS;

/**
 * DataElement Plugin which gets registered at the Stream Processor to debug incoming and outgoing DataElements.
 */
public class DataElementSP implements DataElementPluginInterface, ObservableInterface<DataElementObserverInterface> {

	private static final Collection<DataElementObserverInterface> observers = new ArrayList<>();
	private static final Logger                                   logger    = LogManager.getLogger(PLUGIN_LOGGERS);

	private final PluginSetInterface pluginSet;
	private       BooleanProperty            enabled = new SimpleBooleanProperty(false);

	/**
	 * Constructor of the DataElementSP class.
	 *
	 * @param pluginSet the PluginSet this plugin belongs to.
	 */
	public DataElementSP(PluginSetInterface pluginSet) {
		this.pluginSet = pluginSet;
	}

	@Override
	public Collection<DataElementInterface> incomingDataElement(DataElementInterface data) {
		for (DataElementObserverInterface observer : observers)
			observer.sawIncomingDataElement(data);

		logger.info(OutputFormatter.render("(X)-->StreamProcessor: accepting DataElement", data.toString(), false, true));
		return Collections.singletonList(data);
	}

	@Override
	public Collection<DataElementInterface> outgoingDataElement(DataElementInterface data) {
		for (DataElementObserverInterface observer : observers)
			observer.sawLeavingDataElement(data);

		logger.info(OutputFormatter.render("StreamProcessor-->(X'): passing DataElement", data.toString(), false, false));
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
	public void registerObserver(DataElementObserverInterface observer) {
		if (!observers.contains(observer))
			observers.add(observer);
	}

	@Override
	public void unregisterObserver(DataElementObserverInterface observer) {
		if (observers.contains(observer))
			observers.remove(observer);
	}
}
