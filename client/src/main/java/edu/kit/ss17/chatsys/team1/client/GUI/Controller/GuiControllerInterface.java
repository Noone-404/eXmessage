package edu.kit.ss17.chatsys.team1.client.GUI.Controller;

import edu.kit.ss17.chatsys.team1.client.AccountManager.AccountManagerInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.ConnectionErrorReason;
import edu.kit.ss17.chatsys.team1.client.RosterManager.RosterManagerInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginableInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObservableInterface;

import java.util.Collection;

/**
 * Main class of the GUI component.
 */
public interface GuiControllerInterface extends PluginableInterface, ObservableInterface<GuiObserverInterface> {

	/**
	 * Initializes dependent fields.
	 * <p>
	 * Has to be called once after instance creation.
	 *
	 * @param rosterManager  The roster manager
	 * @param accountManager The account manager
	 */
	void initializeDependentComponents(RosterManagerInterface rosterManager, AccountManagerInterface accountManager);

	void addGlobalPlugin(PluginSetInterface plugin, boolean network);

	/**
	 * Should be called after program has been initialized completely to display the GUI.
	 */
	void showMainWindow();

	/**
	 * Called when a ConnectionStack encounters a protocol error.
	 */
	void setLastErrorMessage(String error);

	/**
	 * Called when search results arrived
	 */
	void provideSearchResults(Collection<String> results);

	void onConnectionSuccess();
	void onConnectionFailed(ConnectionErrorReason reason);
	void onDisconnected();
	void onConnectionDied(AccountConfigurationInterface killedAccount);
}
