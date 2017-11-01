package edu.kit.ss17.chatsys.team1.client.Model;

import edu.kit.ss17.chatsys.team1.client.GUI.ContentFragments.RenderableContentInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.ContentFragments.RenderablePlaintextContent;
import edu.kit.ss17.chatsys.team1.shared.StreamData.Message;
import javafx.scene.Node;

/**
 *
 */
public class RenderablePlaintextMessageStanza extends Message implements RenderableMessageInterface {

	private RenderableContentInterface content;

	public RenderablePlaintextMessageStanza() {
		this.content = new RenderablePlaintextContent();
	}

	@Override
	public Node getContent() {
		return this.content.getContent();
	}

	@Override
	public void setPlaintextRepresentation(String plain) {
		this.content.setPlaintextRepresentation(plain);
		super.setPlaintextRepresentation(plain);
	}

	/* DO NOT SET EXTENDED CONTENT OF RenderablePlaintextContent!
	 * This part is only for reference, if a RenderableRichtextContent-Element should be implemented.
	@Override
	public void setExtendedContent(DataElementInterface extendedContent) {
		this.content.setExtendedContent(extendedContent);
		super.setExtendedContent(extendedContent);
	}
	 */

	@Override
	public String toString() {
		return "RenderablePlaintextMessageStanza{" +
		       "content=" + content +
		       "} " + super.toString();
	}
}
