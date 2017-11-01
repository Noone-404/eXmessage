package edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared;

import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObserverInterface;
import org.dom4j.Document;

/**
 * An observer interface to observe the DocumentSP plugin.
 */
public interface DocumentObserverInterface extends ObserverInterface {

	/**
	 * Called when the plugin logged an incoming Document.
	 *
	 * @param data the Document
	 */
	void sawIncomingDocument(Document data);

	/**
	 * Called when the plugin logged an outgoing Document.
	 *
	 * @param data the Document
	 */
	void sawLeavingDocument(Document data);
}
