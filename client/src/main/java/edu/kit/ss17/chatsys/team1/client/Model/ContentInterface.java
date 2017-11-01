package edu.kit.ss17.chatsys.team1.client.Model;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public interface ContentInterface {

	/**
	 * The plaintext representation of this content element.
	 */
	String getPlaintextRepresentation();

	/**
	 * Sets the plaintext representation of this message.
	 */
	void setPlaintextRepresentation(String plain);

	@Nullable
	DataElementInterface getExtendedContent();

	void setExtendedContent(DataElementInterface extendedContent);
}
