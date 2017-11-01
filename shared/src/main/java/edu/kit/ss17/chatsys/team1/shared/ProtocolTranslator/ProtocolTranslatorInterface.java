package edu.kit.ss17.chatsys.team1.shared.ProtocolTranslator;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObservableInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainDataProcessorInterface;
import org.dom4j.Document;

/**
 * Interface for the Protocol Translator that translates XML Entities into Bytes and vice versa
 */
public interface ProtocolTranslatorInterface extends ChainDataProcessorInterface<byte[], Document>, ProtocolErrorObservableInterface {

}
