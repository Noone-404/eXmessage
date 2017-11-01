package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorType;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StanzaInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Constants;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;

/**
 * This abstract class provides some base functionality for deserializing stanzas.
 */
abstract class AbstractStanzaDeserializer implements DeserializerInterface {

	private static final String ID                  = "id";
	private static final String FROM                = "from";
	private static final String TO                  = "to";
	private static final String TYPE                = "type";
	private static final String ERROR = "error";

	/**
	 * This method translates the XML representation of metadata of a stanza and sets these values in the Stanza object.
	 * @param root the {@link Element} to be deserialized.
	 * @param stanzaInterface the {@link StanzaInterface} in which the data is put.
	 * @return the given {@link StanzaInterface} with the deserialized values set.
	 * @throws StanzaErrorException if the root's XML does not fit our protocol.
	 */
	@Nullable
	static StanzaInterface deserializeMetadata(Element root, StanzaInterface stanzaInterface) throws StanzaErrorException {
		String clientSendDateValue    = null;
		String serverReceiveDateValue = null;
		String clientReceiveDateValue = null;
		String id = null;
		String from = null;
		String to = null;
		String type = null;
		for (final Attribute attribute : (List<Attribute>) root.attributes()) {
			if (attribute.getName().equals(ID)) {
				if (id != null) {
					throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
					                               "Attribute id should only occur once in a stanza.");
				}
				id = attribute.getValue();
			}

			if (attribute.getName().equals(FROM)) {
				if (from != null) {
					throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
					                               "Attribute from should only occur once in a stanza.");
				}
				from = attribute.getValue();
			}

			if (attribute.getName().equals(TO)) {
				if (to != null) {
					throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
					                               "Attribute to should only occur once in a stanza.");
				}
				to = attribute.getValue();
			}

			if (attribute.getName().equals(Constants.CLIENT_SEND_DATE)) {
				if (clientSendDateValue != null) {
					throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
					                               "Attribute client-send-date should only occur once in a stanza.");
				}
				clientSendDateValue = attribute.getValue();
			}

			if (attribute.getName().equals(Constants.SERVER_RECEIVE_DATE)) {
				if (serverReceiveDateValue != null) {
					throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
					                               "Attribute server-receive-date should only occur once in a stanza.");
				}
				serverReceiveDateValue = attribute.getValue();
			}

			if (attribute.getName().equals(Constants.CLIENT_RECEIVE_DATE)) {
				if (clientReceiveDateValue != null) {
					throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
					                               "Attribute client-receive-date should only occur once in a stanza.");
				}
				clientReceiveDateValue = attribute.getValue();
			}

			if (attribute.getName().equals(TYPE)) {
				if (type != null) {
					throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
					                               "Attribute type should only occur once in a stanza.");
				}
				type = attribute.getValue();
			}
		}
		stanzaInterface.setID(id);
		stanzaInterface.setSender(from);
		stanzaInterface.setReceiver(to);
		stanzaInterface.setType(type);

		if (stanzaInterface.getType().equals(ERROR)) { // it's an error element so we're in the wrong deserializer
			return null;
		} else if (stanzaInterface.getID() == null) {
			throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
			                               "Attribute id is missing in the stanza.");
		} else if (stanzaInterface.getSender() == null) {
			throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
			                               "Attribute from is missing in the stanza.");
		} else if (stanzaInterface.getReceiver() == null) {
			throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
			                               "Attribute to is missing in the stanza.");
		} else if (clientSendDateValue == null) {
			throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
			                               "Attribute client-send-date is missing in the stanza.");
		} else if (serverReceiveDateValue == null) {
			throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
			                               "Attribute server-receive-date is missing in the stanza.");
		} else if (clientReceiveDateValue == null) {
			throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
			                               "Attribute client-receive-date is missing in the stanza.");
		} else if (stanzaInterface.getType() == null) {
			throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
			                               "Attribute type is missing in the stanza.");
		}
		stanzaInterface.setClientSendDate(stringToInstant(clientSendDateValue));
		stanzaInterface.setServerReceiveDate(stringToInstant(serverReceiveDateValue));
		stanzaInterface.setClientReceiveDate(stringToInstant(clientReceiveDateValue));

		return stanzaInterface;
	}

	@Nullable
	private static Instant stringToInstant(String instantString) throws StanzaErrorException {
		if (instantString.isEmpty()) {
			return null;
		}
		String[] instantParts = instantString.split("\\.");
		Instant instant;
		try {
			if (instantParts.length == 1) {
				instant = Instant.ofEpochSecond(Long.parseLong(instantParts[0]));
			} else if (instantParts.length == 2) {
				instant = Instant.ofEpochSecond(Long.parseLong(instantParts[0]), Long.parseLong(instantParts[1]));
			} else {
				throw new IllegalArgumentException();
			}
		} catch (Exception e) {
			throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
			                               "Illegal date format in stanza.");
		}
		return instant;
	}
}
