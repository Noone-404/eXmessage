package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorType;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors.StanzaProtocolError;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors.StreamProtocolError;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StreamErrorException;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StreamProcessorException;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.StreamProcessorInterface;
import org.dom4j.Document;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class organizes the translation of a {@link Document} to a {@link DataElementInterface}.
 * It uses an internal Collection of {@link DeserializerInterface}s and lets those do the translation.
 */
public class DeserializationManager implements DeserializationManagerInterface {

	private final StreamProcessorInterface          streamProcessor;
	private       Collection<DeserializerInterface> deserializerInterfaceCollection = new ArrayList<>();

	/**
	 * @param streamProcessor {@link StreamProcessorInterface} to which this object belongs.
	 */
	public DeserializationManager(StreamProcessorInterface streamProcessor) {
		this.streamProcessor = streamProcessor;
	}

	@Override
	public DataElementInterface deserializeXML(Document document) {
		DataElementInterface dataElementInterface = null;
		try {
			for (final DeserializerInterface deserializerInterface : this.deserializerInterfaceCollection) {
				dataElementInterface = deserializerInterface.deserializeXML(document);
				if (dataElementInterface != null) {
					break;
				}
			}

			if (dataElementInterface == null) {
				throw new StreamErrorException(StreamErrorCondition.STANZA_TYPE, "Can't recognize stanza type.");
			}
		} catch (StanzaErrorException e) {
			ProtocolErrorInterface error = new StanzaProtocolError(e.getSender(), e.getReceiver(), e.getStanzaType(), e.getStanzaID(),
			                                                       StanzaErrorType.CONTINUE, StanzaErrorCondition.UNDEFINED, e.getMessage(), false);
			this.streamProcessor.notifyErrorObservers(error);
		} catch (StreamErrorException e) {
			ProtocolErrorInterface error = new StreamProtocolError(e.getStreamErrorCondition(), e.getMessage());
			this.streamProcessor.notifyErrorObservers(error);
		} catch (StreamProcessorException ignore) { // a.k.a. ErrorStanzaErrorException which we don't process to avoid infinite error loop between client and server
		}
		return dataElementInterface;
	}


	@Override
	public void addDeserializerInterface(DeserializerInterface deserializerInterface) {
		this.deserializerInterfaceCollection.add(deserializerInterface);
	}

	@Override
	public void clearDeserializerInterfaceCollection() {
		this.deserializerInterfaceCollection = new ArrayList<>();
	}
}
