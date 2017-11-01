package edu.kit.ss17.chatsys.team1.shared.StreamData;

/**
 * Used to create the opening XML Stream tags.
 */
public interface StreamHeaderInterface extends DataElementInterface {

	/**
	 * Stream ID. Uninitialized if the stream header is created client side.
	 *
	 * @return null or String
	 */
	String getStreamID();

	/**
	 * JID of the initiating entity.
	 */
	String getFrom();

	/**
	 * The servers domain.
	 */
	String getTo();

	/**
	 * E.g. "jabber:client"
	 */
	String getNamespace();
}
