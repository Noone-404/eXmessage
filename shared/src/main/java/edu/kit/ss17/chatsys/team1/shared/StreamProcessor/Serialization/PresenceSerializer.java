package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Constants;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValue;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * This class translates a PresenceInterface into a {@link Document}.
 */
public final class PresenceSerializer extends AbstractStanzaSerializer implements PresenceSerializerInterface {

	private static PresenceSerializerInterface instance;

	private static final String SHOW = "show", CHAT = "chat",
			DO_NOT_DISTURB        = "dnd", AWAY = "away", EXTENDED_AWAY = "xa";

	/**
	 * Private constructor to make Serializers Singletons.
	 */
	private PresenceSerializer() {
	}

	/**
	 * @return this Singleton's instance.
	 */
	public static PresenceSerializerInterface getInstance() {
		return (instance != null) ? instance : (instance = new PresenceSerializer());
	}

	@Override
	public Document serialize(DataElementInterface element) {
		if (!(element instanceof PresenceInterface)) {
			throw new IllegalArgumentException();
		}
		PresenceInterface presence = (PresenceInterface) element;
		Document          document = DocumentHelper.createDocument();
		Element           root     = document.addElement(Constants.PRESENCE);
		root = serializeMetadata(root, presence);
		if (presence.getType().equals(Constants.AVAILABLE)) {
			root.addElement(SHOW).addText(presenceValueToString(presence));
		}
		if (presence.getStatus() != null && !(presence.getStatus().isEmpty())) {
			root.addElement(Constants.STATUS).addText(presence.getStatus());
		}
		return document;
	}

	private static String presenceValueToString(PresenceInterface presence) {
		String presenceValueString;
		switch ((PresenceValue) presence.getPresence()) {
			case ONLINE:
				presenceValueString = Constants.ONLINE;
				break;
			case CHAT:
				presenceValueString = CHAT;
				break;
			case DND:
				presenceValueString = DO_NOT_DISTURB;
				break;
			case AWAY:
				presenceValueString = AWAY;
				break;
			case XA:
				presenceValueString = EXTENDED_AWAY;
				break;
			default:
				throw new IllegalArgumentException();
		}
		return presenceValueString;
	}
}
