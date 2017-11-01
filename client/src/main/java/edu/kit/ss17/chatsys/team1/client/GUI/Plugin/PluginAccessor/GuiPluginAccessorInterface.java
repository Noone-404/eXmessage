package edu.kit.ss17.chatsys.team1.client.GUI.Plugin.PluginAccessor;

import edu.kit.ss17.chatsys.team1.client.Model.ContentInterface;
import edu.kit.ss17.chatsys.team1.client.Model.TextContentFragmentInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObservableInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import javafx.scene.control.IndexRange;

import java.util.function.BiConsumer;

/**
 * Used by input editor plugins to retrieve necessary information from the gui, when one of their defined buttons/controls triggers an event.
 */
public interface GuiPluginAccessorInterface extends ObservableInterface<PluginGuiObserver> {

	/**
	 * Get text content from within the input area.
	 */
	TextContentFragmentInterface getContentTree();

	/**
	 * Allows the plugin to send modified input content to the gui which will be displayed within the input area.
	 */
	void setContentTree(TextContentFragmentInterface tree);


	/**
	 * Get the selected text range from within the input area
	 */
	IndexRange getSelectionRange();

	/**
	 * Sets the selection range
	 *
	 * @param selectionRange The new selection range
	 */
	void setSelectionRange(IndexRange selectionRange);

	/**
	 * Get the selected contact.
	 *
	 * @return the selected contact
	 */
	ContactInterface getSelectedContact();

	/**
	 * Sets the currently selected contact.
	 *
	 * @param contact the contact
	 */
	void setSelectedContact(ContactInterface contact);

	/**
	 * Get the JID of the currently logged in user.
	 */
	JID getLoggedInJID();

	/**
	 * Sets the JID of the currently logged in user.
	 *
	 * @param jid the JID
	 */
	void setLoggedInJID(JID jid);

	/**
	 * Send a message to the specified contact.
	 *
	 * @param contact the recipient
	 * @param content the message content
	 */
	void sendMessage(ContactInterface contact, ContentInterface content);

	/**
	 * Sets the consumer for sent messages.
	 *
	 * @param consumer the consumer, given by GuiController.
	 */
	void setSendMessageConsumer(BiConsumer<ContentInterface, ContactInterface> consumer);
}
