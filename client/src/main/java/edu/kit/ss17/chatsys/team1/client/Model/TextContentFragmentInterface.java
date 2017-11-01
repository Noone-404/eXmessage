package edu.kit.ss17.chatsys.team1.client.Model;

import edu.kit.ss17.chatsys.team1.shared.Util.StyleInterface;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a piece of the message with its own style. It can have child elements if it also contains other styled text. Rendering must start from the most outer level.
 */
public interface TextContentFragmentInterface extends ContentInterface {

	/*
	 * Content consists of Strings as long as they do or do not EXACTLY match the style given by <tt>String getStyle()</tt>.
	 * <p>
	 * Example:
	 * <p>
	 * <tt>H<strong>a<u>ll</u>o</strong> Welt</tt> is represented like this:
	 * <p>
	 * <tt>ContentInterface { // Root element
	 * . content: ""
	 * . children: [ContentInterface { content: "H", style: null, children: null },
	 * .          ContentInterface { content: "allo", style: bold,
	 * .                                     children : [ContentInterface { content: "a", style: null, children: null },
	 * .                                               ContentInterface { content: "ll", style: underline, children: null }
	 * .                                              ]
	 * .                                   },
	 * .          ContentInterface { content: " Welt", style: null, children: null }
	 * .         ]
	 * . style: default
	 * }</tt>
	 * <p>
	 * To render this construct, begin at the top level, loop through the children and apply all collected styles on the way down until a child doesn't have children on its own.
	 *
	 */

	@Nullable
	List<TextContentFragmentInterface> getChildren();

	@Nullable
	StyleInterface getStyle();
}
