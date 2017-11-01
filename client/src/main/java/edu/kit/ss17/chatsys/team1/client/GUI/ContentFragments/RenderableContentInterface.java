package edu.kit.ss17.chatsys.team1.client.GUI.ContentFragments;

import edu.kit.ss17.chatsys.team1.client.Model.ContentInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import javafx.scene.Node;

/**
 *
 */
public interface RenderableContentInterface extends ContentInterface {

	/**
	 * Real content (extended content) of the fragment
	 *
	 * @return Node
	 */
	Node getContent();
}
