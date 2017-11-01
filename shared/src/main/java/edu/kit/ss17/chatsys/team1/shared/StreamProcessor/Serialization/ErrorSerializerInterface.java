package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.ErrorElement;
import org.dom4j.Document;

/**
 * Implementing classes must provide functionality to translate an {@link ErrorElement} into a {@link Document}
 */
public interface ErrorSerializerInterface extends DataElementSerializerInterface {

}
