package edu.kit.ss17.chatsys.team1.shared.StreamData;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.PresenceSerializerInterface;
import org.dom4j.Document;

/**
 * Presence objects represent a client's status.
 */
public class Presence extends AbstractStanza implements PresenceInterface {

	private static PresenceSerializerInterface presenceSerializer;
	private        PresenceValueInterface      presenceValueInterface = PresenceValue.OFFLINE;
	private String status = "";

	/**
	 * @param presenceSerializer the serializer that is responsible for translating this object to a {@link Document}.
	 */
	public static void setPresenceSerializer(PresenceSerializerInterface presenceSerializer) {
		Presence.presenceSerializer = presenceSerializer;
	}

	/**
	 * @return the serializer that is responsible for translating this object to a {@link Document}.
	 */
	public static PresenceSerializerInterface getPresenceSerializer() {
		return presenceSerializer;
	}

	@Override
	public Document serialize() {
		return presenceSerializer.serialize(this);
	}

	@Override
	public PresenceValueInterface getPresence() {
		return presenceValueInterface;
	}

	@Override
	public void setPresence(PresenceValueInterface newValue) {
		this.presenceValueInterface = newValue;
	}

	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public void setStatus(String newStatus) {
		this.status = newStatus;
	}

	@Override
	public String toString() {
		return "Presence{" +
		       "presenceValueInterface=" + presenceValueInterface +
		       ", status='" + status + '\'' +
		       "} " + super.toString();
	}
}
