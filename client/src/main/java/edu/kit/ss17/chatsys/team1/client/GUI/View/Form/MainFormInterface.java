package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import edu.kit.ss17.chatsys.team1.client.GUI.ContentFragments.RenderableContentInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.ConnectionErrorReason;
import edu.kit.ss17.chatsys.team1.client.Model.ContentInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValueInterface;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.util.Pair;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 *
 */
public interface MainFormInterface extends FormBaseInterface {

	void setLocales(Map<String, Pair<String, Boolean>> locales);
	StringProperty getSelectedLanguageProperty();
	void setPluginManagerDialog(PluginManagerDialog pluginManagerDialog);
	void setAccountConfigurationDialog(AccountConfigurationDialogInterface accountConfigurationDialog);
	void setAddContactDialog(AddContactDialogInterface addContactDialog);
	void setAccountConfigurations(ObservableList<? extends AccountConfigurationInterface> accountConfigurations);
	void setLastErrorProperty(ReadOnlyStringProperty lastErrorProperty);
	void setRoster(RosterInterface roster);
	void connectionEstablished();

	void setOnConnectRequestedCallback(Consumer<AccountConfigurationInterface> consumer);
	void setOnDisconnectRequestedCallback(Consumer<AccountConfigurationInterface> consumer);
	void setPresenceChangeCallback(Consumer<PresenceValueInterface> newPresence);
	void setContactDeletedConsumer(Consumer<ContactInterface> deletedContact);
	void setSelectedContactChangedCallback(Consumer<ContactInterface> consumer);
	void setMessageSendRequestedCallback(BiConsumer<ContentInterface, ContactInterface> consumer);
	void setMessageFetchFunction(BiFunction<AccountConfigurationInterface, ContactInterface, Collection<MessageInterface>> fetcher);
	void setAccountConfigurationDeletedConsumer(Consumer<AccountConfigurationInterface> consumer);
	void showConnectionFailedMessage(ConnectionErrorReason reason);
	void processDisconnectResult(boolean success);
	void onConnectionDied(AccountConfigurationInterface accountConfiguration);

	/**
	 * Called by ViewManager when a message was received.
	 *
	 * @param participatingContact the participating contact
	 * @param sender               the sender's JID
	 * @param msg                  the message
	 * @param otherSend            time when the message was sent
	 * @param serverReceived       time when the message arrived at the server
	 * @param selfReceived         time when the message was received
	 */
	void onMessageReceived(ContactInterface participatingContact,
	                       String sender,
	                       RenderableContentInterface msg,
	                       Instant otherSend,
	                       Instant serverReceived,
	                       Instant selfReceived);

	/**
	 * Sets menu items created by GuiMenuPluginInterface plugins.
	 *
	 * @param items the menu items
	 */
	void setPluginMenuItems(Collection<MenuItem> items);
}
