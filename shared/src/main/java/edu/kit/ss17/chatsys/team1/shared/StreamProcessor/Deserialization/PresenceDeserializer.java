package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorType;
import edu.kit.ss17.chatsys.team1.shared.StreamData.*;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Constants;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.jetbrains.annotations.Nullable;

/**
 * This class deserializes Presence stanzas.
 */
public class PresenceDeserializer extends AbstractStanzaDeserializer {

	private static final String SHOW      = "show";
	private static final String CHAT = "chat", DO_NOT_DISTURB = "dnd", AWAY = "away", EXTENDED_AWAY = "xa", PROBE = "probe";

	@Nullable
	@Override
	public DataElementInterface deserializeXML(Branch branch) throws StanzaErrorException {
		PresenceInterface presence = new Presence();
		Element           root     = ((Document) branch).getRootElement();
		if (root == null) {
			throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
			                               Constants.EMPTY_XML_MESSAGE);
		}
		if (root.getName().equals(Constants.PRESENCE)) {
			presence = (PresenceInterface) deserializeMetadata(root, presence);
			if (presence != null) { // if presence == null then deserializeMetadata decided it's not a stanza
				if (root.elements().isEmpty()) {
					if (presence.getType().equals(Constants.AVAILABLE)) {
						presence.setPresence(PresenceValue.ONLINE);
					} else if (presence.getType().equals(Constants.UNAVAILABLE)) {
						presence.setPresence(PresenceValue.OFFLINE);
					} else if (!(presence.getType().equals(PROBE))) {
						throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.PRESENCE, presence.getSender(),
						                               presence.getReceiver(), presence.getID(), "Invalid type value, should be "
						                               + Constants.AVAILABLE + ", " + Constants.UNAVAILABLE + " or " + PROBE + '.');
					}
					presence.setStatus("");
				} else if (root.elements().size() == 1) { // TODO make sure user is not offline and online at the same time
					Element child = (Element) (root.elements().get(0));
					if (child.getName().equals(SHOW)) {
						presence = setPresenceValue(presence, child.getText());
					} else if (child.getName().equals(Constants.STATUS)) {
						presence.setStatus(child.getText());
					}
				} else if (root.elements().size() == 2) {
					presence = setPresenceValue(presence, ((Element) root.elements().get(0)).getText());
					presence.setStatus(((Node) root.elements().get(1)).getText());
				} else {
					throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.PRESENCE, presence.getSender(),
					                               presence.getReceiver(), presence.getID(), "Invalid number of children, should be between 0 and 2.");
				}
			}
		} else {
			presence = null;
		}

		return presence;
	}

	private static PresenceInterface setPresenceValue(PresenceInterface presence, String presenceValue) throws StanzaErrorException {
		PresenceValueInterface presenceValueInterface;
		switch (presenceValue) {
			case Constants.ONLINE:
				presenceValueInterface = PresenceValue.ONLINE;
				break;
			case CHAT:
				presenceValueInterface = PresenceValue.CHAT;
				break;
			case DO_NOT_DISTURB:
				presenceValueInterface = PresenceValue.DND;
				break;
			case AWAY:
				presenceValueInterface = PresenceValue.AWAY;
				break;
			case EXTENDED_AWAY:
				presenceValueInterface = PresenceValue.XA;
				break;
			default:
				throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.PRESENCE, presence.getSender(),
				                               presence.getReceiver(), presence.getID(), "Invalid presence value.");
		}
		presence.setPresence(presenceValueInterface);
		return presence;
	}
}
