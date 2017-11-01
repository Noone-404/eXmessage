package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Plugins;

import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;

import java.util.Collection;

/**
 * A plugin to manipulate incoming and outgoing data elements.
 */
public interface DataElementPluginInterface extends PluginInterface {

	/**
	 * Processes an incoming DataElement
	 *
	 * @param data The DataElement that was received.
	 *
	 * @return Plugins may return the same, a modified or any number of new created DataElementInterfaces or a combination of that.
	 */
	Collection<DataElementInterface> incomingDataElement(DataElementInterface data);

	/**
	 * Processes an outgoing DataElement
	 *
	 * @param data The DataElement to send.
	 *
	 * @return Plugins may return the same, a modified or any number of new created DataElementInterfaces or a combination of that.
	 */
	Collection<DataElementInterface> outgoingDataElement(DataElementInterface data);
}
