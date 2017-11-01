package edu.kit.ss17.chatsys.team1.shared.StreamData;

import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObservableInterface;
import org.dom4j.Branch;

/**
 * MessageInterface objects represent the client / client chat interaction. They can contain plaintext and / or extended content.
 */
public interface MessageInterface extends StanzaInterface, ObservableInterface<MessageObserverInterface> {

	/**
	 * @return String Falls der Contenttype des ExtendedContent nicht unterstützt wird, oder nicht existiert, wird dieser Text dargestellt.
	 */
	String getPlaintextRepresentation();

	/**
	 * Sets the plaintext representation of this message.
	 */
	void setPlaintextRepresentation(String plain);

	/**
	 * @return Collection von DataElementInterfaces Eine Auflistung an DataElementen, die ExtendedContent darstellen
	 */
	DataElementInterface getExtendedContent();

	/**
	 * Sets the extended content.
	 */
	void setExtendedContent(DataElementInterface extendedContent);

	/**
	 * @return String the XML encoding of the extended content. This can be used if the extended content can't be serialized.
	 */
	String getExtendedContentXML();

	/**
	 * @param extendedContentXML String the XML encoding of the extended content. This method should be used if an extended content {@link Branch} can't be deserialized.
	 */
	void setExtendedContentXML(String extendedContentXML);
}
