package edu.kit.ss17.chatsys.team1.shared.StreamProcessor;


import edu.kit.ss17.chatsys.team1.shared.ConnectionStack.ConnectionStackBaseInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginableInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObservableInterface;
import edu.kit.ss17.chatsys.team1.shared.Session.SessionBaseInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainDataProcessorInterface;
import org.dom4j.Document;

/**
 * This interface declares the functionality of a StreamProcessor.
 * A StreamProcessor organizes the translation of {@link Document} to {@link DataElementInterface} and vice versa.
 */
public interface StreamProcessorInterface extends PluginableInterface, ChainDataProcessorInterface<Document, DataElementInterface>, ProtocolErrorObservableInterface {

	/**
	 * This method notifies all error observers of an error that happened.
	 * @param error the error that happened.
	 */
	void notifyErrorObservers(ProtocolErrorInterface error);

	/**
	 * Tells the stream processor which session to use.
	 *
	 * @param session the session.
	 */
	void setSession(SessionBaseInterface session);

	/**
	 * @param connectionStackBase {@link ConnectionStackBaseInterface} to send stanzas to.
	 */
	void setConnectionStackBase(ConnectionStackBaseInterface connectionStackBase);
}
