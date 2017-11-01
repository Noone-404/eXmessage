package edu.kit.ss17.chatsys.team1.shared.ProtocolError;

/**
 * Contains all constants used to define {@Link AbstractProtocolError} subclasses.
 */
public final class ErrorConstants {

	public enum StreamErrorCondition {

		BAD_FORMAT("bad-format"), // generally used for XML that cannot be processed; may be replaced by a more accurate error type (e.g. bad namespace prefix)

		BAD_NAMESPACE_PREFIX("bad-namespace-prefix"), // missing or unsupported namespace prefix

		TIMEOUT("connection-timeout"),

		HOST_GONE("host-gone"), // 'to' attribute refers to a service at this server that no longer exists

		HOST_UNKNOWN("host-unknown"), // 'to' attribute refers to an unknown host address

		ADDRESSING("improper-addressing"), // 'from' and/or 'to' attribute is either missing or empty or is not a valid XMPP address

		INVALID_FROM("invalid-from"), // 'from' attribute is not an authorized JID or a validated domain

		INVALID_NAMESPACE("invalid-namespace"), // 'stream' namespace is either something other than "http://etherx.jabber.org/streams"
		// or the default content namespace is unsupported (e.g. something other than "jabber:client" or "jabber:server")

		NOT_WELL_FROMED("not-well-formed"), // received XML that was was not well-formed (e.g. usage of undeclared namespace prefix, no closing tag etc.)

		RESTRICTED("restricted-xml"), // recieved restricted XML features (e.g. comment, processing instruction etc.)

		SHUTDOWN("system-shutdown"), // server is being shut down; all active streams are being closed

		UNDEFINED("undefined-condition"), // should only be used with application-specific error conditions

		ENCODING("unsupported-encoding"), // used encoding is not UTF-8

		UNSUPPORED_FEATURE("unsupported-feature"), // received only mandatory features that are not supported by the client

		STANZA_TYPE("unsupported-stanza-type"), // received unsupported first-level child of the 'stream' element (either unsupported namespace or
		// unsupported element)

		VERSION("unsupported-version"), // received unsupported version of XMPP (attribute 'version' of 'stream' element)

		INTERNAL_ERROR("internal-server-error"), // e.g. misconfiguration

		AUTHORIZATION("not-authorized"), // client attempted to send stanzas before stream negotiation was complete

		POLICY("policy-violation"), // violated local policy (e.g. stanza size limit)
		// policy

		RESOURCE_CONSTRAINT("resource-constraint"); //server / recipient is busy or lacks resources to proceed XML

		private final String name;

		StreamErrorCondition(String condition) {
			this.name = condition;
		}

		public String getName() {
			return this.name;
		}

		public static StreamErrorCondition parseValue(String value) {
			for (StreamErrorCondition condition : StreamErrorCondition.values()) {
				if (condition.getName().equals(value)) {
					return condition;
				}
			}
			throw new IllegalArgumentException("No valid error condition named " + value + " found!");
		}
	}


	public enum StanzaErrorType {

		AUTH("auth"), // retry after providing credentials

		CANCEL("cancel"), // do not retry

		CONTINUE("continue"), // condition was only a warning

		MODIFY("modify"), // retry after changing the data sent

		WAIT("wait"); // error is temporary; retry after waiting

		private final String name;

		StanzaErrorType(String type) {
			this.name = type;
		}

		public String getName() {
			return this.name;
		}

		public static StanzaErrorType parseValue(String value) {
			for (StanzaErrorType type : StanzaErrorType.values()) {
				if (type.getName().equals(value)) {
					return type;
				}
			}
			throw new IllegalArgumentException("No valid error type named " + value + " found!");
		}
	}

	public enum StanzaErrorCondition {

		BAD_REQUEST("bad-request"),    // stanza contains XML that cannot be processed (e.g. wrong attribute value, wrong
		// element syntax etc.); recommended type is 'modify'

		CONFLICT("conflict"), // tried to create resource with a name and/or address that already exists;
		// recommended type is 'cancel'

		FEATURE("feature-not-implemented"), // recommended type is 'modify' or 'cancel'

		FORBIDDEN("forbidden"), // sender does not have the authorization to perform the specified action; recommended type is
		// 'auth'

		GONE("gone"), // recipient or server can no longer be contacted at the specified address
		// (should contain a new one as data of 'gone' element); recommended type is 'cancel'

		ITEM_NOT_FOUND("item-not-found"), // addressed JID or requested item cannot be found; recommended type is 'cancel'

		MALFORMED_JID("jid-malformed"), // recommended type is 'modify'

		NOT_ACCEPTABLE("not-acceptable"), // e.g. subscription request without necessary parameters; recommended type is 'modify'

		NOT_ALLOWED("not-allowed"), // attempted action is not allowed (e.g. sending to entities at a blacklisted domain);
		// recommended type is 'cancel'

		RECIPIENT_UNAVAILABLE("recipient-unavailable"), // intended recipient is temporarily unavailable; recommended type is
		// 'wait'

		REGISTRATION_REQUIRED("registration-required"), // registration is necessary before accessing the requested service (e.g
		// . members-only multi-user chat)
		// ; recommended type is 'auth'

		SERVER_NOT_FOUND("remote-server-not-found"), // JID of intended recipient does not exist or can't be resolved;
		// recommended type is 'cancel'

		SERVER_TIMEOUT("remote-server-timeout"), // JID of intended recipient was resolved but communications can't be
		// established;
		// recommended type is 'wait'

		SERVICE_UNAVAILABLE("service-unavailable"), // is used instead of 'item-not-found' or 'recipient-unavailable' if the
		// requesting entity is not
		// authorized to know the intended recipient's network availability, recommended type is 'cancel'

		SUBSCRIPTION_REQUIRED("subscription-required"), // e.g. for presence information; recommended type is 'auth'

		UNDEFINED("undefined-condition"), // should only be used with application-specific error conditions, any type can be used with this

		UNEXPECTED_REQUEST("unexpected-request"), // recipient / server understood the request but was not expecting it (e.g.
		// request out of order);		//
		// recommended type is 'wait' or 'modify'

		INTERNAL_ERROR("internal-server-error"), // e.g. misconfiguration; recommended type is 'cancel'

		AUTHORIZATION("not-authorized"), // client attempted to send stanzas before stream negotiation was complete; recommended type is 'auth'

		POLICY("policy-violation"), // violated local policy (e.g. stanza size limit); recommended type is 'modify' or 'wait' depending on violated
		// policy

		RESOURCE_CONSTRAINT("resource-constraint"); //server / recipient is busy or lacks resources to proceed XML; recommended type is 'wait'

		private final String name;

		StanzaErrorCondition(String condition) {
			this.name = condition;
		}

		public String getName() {
			return this.name;
		}

		public static StanzaErrorCondition parseValue(String value) {
			for (StanzaErrorCondition condition : StanzaErrorCondition.values()) {
				if (condition.getName().equals(value)) {
					return condition;
				}
			}
			throw new IllegalArgumentException("No valid error condition named " + value + " found!");
		}
	}
}
