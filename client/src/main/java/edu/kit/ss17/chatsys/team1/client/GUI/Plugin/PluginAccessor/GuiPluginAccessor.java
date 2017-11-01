package edu.kit.ss17.chatsys.team1.client.GUI.Plugin.PluginAccessor;

import edu.kit.ss17.chatsys.team1.client.GUI.Controller.GuiObserverInterface;
import edu.kit.ss17.chatsys.team1.client.Model.ContentInterface;
import edu.kit.ss17.chatsys.team1.client.Model.TextContentFragmentInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import javafx.scene.control.IndexRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Allows plugins to listen for gui events, get gui information and set gui information.
 */
public class GuiPluginAccessor implements GuiPluginAccessorInterface {

	private final Collection<PluginGuiObserver> observers = new ArrayList<>();

	private final Consumer<TextContentFragmentInterface> textContentFragmentConsumer;
	private final Supplier<TextContentFragmentInterface> textContentFragmentSupplier;

	private final Consumer<IndexRange> indexRangeConsumer;
	private final Supplier<IndexRange> indexRangeSupplier;

	private BiConsumer<ContentInterface, ContactInterface> sendMessage;

	private ContactInterface selectedContact;
	private JID              loggedInJID;

	public GuiPluginAccessor(
			/* General events        */ Consumer<GuiObserverInterface> guiObserverConsumer,
			/* Access to textContent */ Consumer<TextContentFragmentInterface> textContentFragmentConsumer, Supplier<TextContentFragmentInterface> textContentFragmentSupplier,
			/* Access to indexRange  */ Consumer<IndexRange> indexRangeConsumer, Supplier<IndexRange> indexRangeSupplier,
			/* Send Messages         */ BiConsumer<ContentInterface, ContactInterface> sendMessage) {

		guiObserverConsumer.accept(new GuiObserverInterface() {
			@Override
			public void onSelectedContactChanged(ContactInterface contact) {
				GuiPluginAccessor.this.observers.forEach(pluginGuiObserver -> pluginGuiObserver.selectedContactChanged(contact));
			}

			@Override
			public void onInputChanged() {
				GuiPluginAccessor.this.observers.forEach(PluginGuiObserver::inputChanged);
			}
		});

		this.textContentFragmentConsumer = textContentFragmentConsumer;
		this.textContentFragmentSupplier = textContentFragmentSupplier;

		this.indexRangeConsumer = indexRangeConsumer;
		this.indexRangeSupplier = indexRangeSupplier;

		this.sendMessage = sendMessage;
	}

	@Override
	public TextContentFragmentInterface getContentTree() {
		return this.textContentFragmentSupplier.get();
	}

	@Override
	public void setContentTree(TextContentFragmentInterface tree) {
		this.textContentFragmentConsumer.accept(tree);
	}

	@Override
	public IndexRange getSelectionRange() {
		return this.indexRangeSupplier.get();
	}

	@Override
	public void setSelectionRange(IndexRange selectionRange) {
		this.indexRangeConsumer.accept(selectionRange);
	}

	@Override
	public ContactInterface getSelectedContact() {
		return this.selectedContact;
	}

	@Override
	public void setSelectedContact(ContactInterface contact) {
		this.selectedContact = contact;
	}

	@Override
	public JID getLoggedInJID() {
		return this.loggedInJID;
	}

	@Override
	public void setLoggedInJID(JID jid) {
		this.loggedInJID = jid;
	}

	@Override
	public void sendMessage(ContactInterface contact, ContentInterface content) {
		this.sendMessage.accept(content, contact);
	}

	@Override
	public void setSendMessageConsumer(BiConsumer<ContentInterface, ContactInterface> consumer) {
		this.sendMessage = consumer;
	}

	@Override
	public void registerObserver(PluginGuiObserver observer) {
		this.observers.add(observer);
	}

	@Override
	public void unregisterObserver(PluginGuiObserver observer) {
		this.observers.remove(observer);
	}
}
