package edu.kit.ss17.chatsys.team1.client.GUI.ContentFragments;

import edu.kit.ss17.chatsys.team1.client.Model.PlainTextContentFragment;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 *
 */
public class RenderablePlaintextContent extends PlainTextContentFragment implements RenderableContentInterface {

	private Node content;

	@Override
	public Node getContent() {
		return this.content == null ? (this.content = buildNode()) : this.content;
	}

	private Node buildNode() {
		Label label = new Label(this.getPlaintextRepresentation());
		label.setWrapText(true);
		return label;
	}
}
