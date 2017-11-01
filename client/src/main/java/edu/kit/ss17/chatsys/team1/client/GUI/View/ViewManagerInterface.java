package edu.kit.ss17.chatsys.team1.client.GUI.View;

import edu.kit.ss17.chatsys.team1.client.GUI.Internationalization.LocaleInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.ConnectionErrorReason;
import edu.kit.ss17.chatsys.team1.client.Model.ContentInterface;
import edu.kit.ss17.chatsys.team1.client.Model.RenderableMessageInterface;
import edu.kit.ss17.chatsys.team1.client.Model.TextContentFragmentInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValueInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.MenuItem;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 */
public interface ViewManagerInterface {

	// Callbacks - invoked by ViewManager
	void setContactAddedConsumer(Consumer<JID> consumer);
	void setContactRemovedConsumer(Consumer<JID> consumer);
	void setConnectRequestedConsumer(Consumer<AccountConfigurationInterface> consumer);
	void setDisconnectFunction(Function<AccountConfigurationInterface, CompletableFuture<Boolean>> consumer);
	void setSendMessageConsumer(BiConsumer<ContentInterface, ContactInterface> consumer);
	void setAccountAddedConsumer(Consumer<AccountConfigurationInterface> consumer);
	void setAccountRemovedConsumer(Consumer<AccountConfigurationInterface> consumer);
	void setAccountChangedConsumer(Consumer<AccountConfigurationInterface> consumer);
	void setSelectedContactChangedConsumer(Consumer<ContactInterface> consumer);
	void setInputChangedConsumer(Consumer<Void> consumer);
	void setLastFormClosedConsumer(Consumer<Void> consumer);
	void setContactSearchConsumer(Consumer<String> consumer);

	// Callbacks invoked by outside components
	void onConnectionEstablished();
	void onConnectionFailed(ConnectionErrorReason reason);
	void onConnectionDied(AccountConfigurationInterface killedAccount);

	/**
	 * Called if the GuiController received an incoming message.
	 *
	 * @param contact the sender
	 * @param message the message
	 */
	void onMessageReceived(ContactInterface contact, RenderableMessageInterface message);

	// actions, getters, setters - invoked by anyone who has got this object
	void buildViews();
	void showMainWindow();
	void setLastErrorMessage(String errorMessage);
	void setRoster(RosterInterface roster);

	// semi-actions: getters & setters
	TextContentFragmentInterface getTextContent();
	void setTextContent(TextContentFragmentInterface content);

	IndexRange getIndexRange();
	void setIndexRange(IndexRange indexRange);

	// Special type of actions: Initialisations. Should be invoked only once prior to displaying the main form.
	void setPluginMenuItems(Collection<MenuItem> items);
	void setPluginControls(Collection<Node> controls);
	void setPlugins(Collection<PluginSetInterface> plugins, boolean isNetwork);
	void setAccountList(Collection<AccountConfigurationInterface> accounts);

	// Properties
	ObjectProperty<PresenceValueInterface> currentPresenceProperty();
	void provideSearchResults(Collection<String> results);


	/**
	 * Gets the current locale
	 *
	 * @return The current locale
	 */
	LocaleInterface getCurrentLocale();
}
