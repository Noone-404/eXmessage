package edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObserverInterface;

/**
 * An observer interface to observe the DataElementSP plugin.
 */
public interface DataElementObserverInterface extends ObserverInterface {

	/**
	 * Called when the plugin logged an incoming DataElement.
	 *
	 * @param data the DataElement
	 */
	void sawIncomingDataElement(DataElementInterface data);

	/**
	 * Called when the plugin logged an outgoing DataElement.
	 *
	 * @param data the DataElement
	 */
	void sawLeavingDataElement(DataElementInterface data);
}
