package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Plugins;

import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;
import org.dom4j.Document;

import java.util.Collection;

/**
 * A plugin to manipulate incoming and outgoing Documents.
 */
public interface DocumentPluginInterface extends PluginInterface {

	/**
	 * Processes an incoming Document
	 *
	 * @param data The Document that was received.
	 *
	 * @return Plugins may return the same, a modified or any number of new created Documents or a combination of that.
	 */
	Collection<Document> incomingDocument(Document data);

	/**
	 * Processes an outgoing Document
	 *
	 * @param data The Document to send.
	 *
	 * @return Plugins may return the same, a modified or any number of new created Documents or a combination of that.
	 */
	Collection<Document> outgoingDocument(Document data);
}
