package edu.kit.ss17.chatsys.team1.client.Model;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.StyleInterface;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a piece of the message with its own style. It can have child elements if it also contains other styled text. Rendering must start from the most outer level.
 */
public class PlainTextContentFragment implements TextContentFragmentInterface {

	private String plaintextRepresentation;

	public PlainTextContentFragment() {
		this("");
	}

	public PlainTextContentFragment(String content) {
		this.plaintextRepresentation = content;
	}

	@Nullable
	@Override
	public List<TextContentFragmentInterface> getChildren() {
		return null;
	}

	@Nullable
	@Override
	public StyleInterface getStyle() {
		return null;
	}

	@Override
	public String getPlaintextRepresentation() {
		return this.plaintextRepresentation;
	}

	@Override
	public void setPlaintextRepresentation(String plain) {
		this.plaintextRepresentation = plain;
	}

	@Nullable
	@Override
	public DataElementInterface getExtendedContent() {
		return null;
	}

	@Override
	public void setExtendedContent(DataElementInterface extendedContent) {
		throw new UnsupportedOperationException("Cannot set extended content of plaintext content element.");
	}
}
