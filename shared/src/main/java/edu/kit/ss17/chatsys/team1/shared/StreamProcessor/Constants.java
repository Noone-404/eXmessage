package edu.kit.ss17.chatsys.team1.shared.StreamProcessor;

/**
 * This class contains the constants used in the StreamProcessor.
 */
public final class Constants {

	/**
	 * Used as the outer tag in presence stanzas.
	 */
	public static final String PRESENCE = "presence";

	/**
	 * Attribute of the outer tag in presence stanzas.
	 */
	public static final String STATUS = "status";

	/**
	 * String value of the presence show online.
	 */
	public static final String ONLINE = "online";

	/**
	 * Attribute value of type in presence stanzas.
	 */
	public static final String AVAILABLE = "available";

	/**
	 * Attribute value of type in presence stanzas for presence offline.
	 */
	public static final String UNAVAILABLE = "unavailable";

	/**
	 * Used as the outer tag in message stanzas.
	 */
	public static final String MESSAGE = "message";

	/**
	 * Used as parameter in the outer tag of stanzas.
	 */
	public static final String CLIENT_SEND_DATE = "client-send-date";

	/**
	 * Used as parameter in the outer tag of stanzas.
	 */
	public static final String SERVER_RECEIVE_DATE = "server-receive-date";

	/**
	 * Used as parameter in the outer tag of stanzas.
	 */
	public static final String CLIENT_RECEIVE_DATE = "client-receive-date";

	/**
	 * Error message for an empty XML document.
	 */
	public static final String EMPTY_XML_MESSAGE = "XML element should not be empty.";

	/**
	 * Stanza type for stanza errors when the original stanza type is unknown.
	 */
	public static final String ERROR = "error";

	/**
	 * Used as the outer tag of IQ stanzas.
	 */
	public static final String IQ = "iq";

	private Constants() {
	}
}
