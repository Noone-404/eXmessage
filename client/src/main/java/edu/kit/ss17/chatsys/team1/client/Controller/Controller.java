package edu.kit.ss17.chatsys.team1.client.Controller;

import edu.kit.ss17.chatsys.team1.client.AccountManager.AccountManager;
import edu.kit.ss17.chatsys.team1.client.GUI.Controller.GuiController;
import edu.kit.ss17.chatsys.team1.client.GUI.Controller.GuiControllerInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.Controller.GuiObserverInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.ConnectionErrorReason;
import edu.kit.ss17.chatsys.team1.client.RosterManager.RosterManager;
import edu.kit.ss17.chatsys.team1.client.Storage.Storage;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkProtocolListenerPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkProtocolPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginManagerFactory;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginManagerInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 * Default client controller.
 */
public class Controller implements ControllerInterface {

	private static final Logger logger = LogManager.getLogger(APP_NAME);
	private static Controller instance;

	private final Map<AccountConfigurationInterface, ConnectionStackInterface> connectionStacks = new HashMap<>();

	private Controller() {
		AnsiConsole.systemInstall();

		GuiController.getInstance().initializeDependentComponents(RosterManager.getInstance(), AccountManager.getInstance());

		AccountManager.getInstance().setStorage(Storage.getInstance());
		RosterManager.getInstance().setStorage(Storage.getInstance());
		PluginManagerFactory.setRoot(Controller.class);
		PluginManagerFactory.getInstance().setStorage(Storage.getInstance());

		GuiController.getInstance().registerObserver(new GuiObserverInterface() {
			@Override
			public void onConnect(AccountConfigurationInterface account, RosterInterface roster) {
				GuiControllerInterface gui = GuiController.getInstance();
				if (Controller.this.connectionStacks.containsKey(account)) {
					logger.warn("Cannot initiate connection stack creation because connection stack for selected account already exists!");
					gui.onConnectionFailed(ConnectionErrorReason.GENERAL);
				} else {
					try {
						Controller.this.connectionStacks.put(account, new ConnectionStack(account, gui, roster));
					} catch (IOException e) {
						gui.setLastErrorMessage(e.getLocalizedMessage() + " (" + e.getClass().getSimpleName() + ')');
						gui.onConnectionFailed(ConnectionErrorReason.SERVER);
					}
				}

			}
		});

		logger.info("components initialized");

		PluginManagerInterface globalManager = PluginManagerFactory.make();
		globalManager.registerPluginable(GuiController.getInstance());
		globalManager.registerPlugins();

		logger.info("plugins loaded");

		for (PluginSetInterface psi : PluginManagerFactory.getInstance().getPluginSetList())
			GuiController.getInstance().addGlobalPlugin(psi,
			                                            psi.getPlugins().stream().anyMatch(pluginInterface -> pluginInterface instanceof NetworkProtocolPluginInterface ||
			                                                                                                  pluginInterface instanceof NetworkProtocolListenerPluginInterface));

	}

	public static Controller getInstance() {
		return (instance != null) ? instance : (instance = new Controller());
	}

	@Override
	public void closeConnectionStack(ConnectionStackInterface stack) {
		logger.info("Closing ConnectionStack");
		this.connectionStacks.remove(stack.getAccountConfiguration());
	}

	@SuppressWarnings("MethodMayBeStatic") // must not be static to ensure initialisation has occurred prior to function call
	public void runApplication() {
		GuiController.getInstance().showMainWindow();
		logger.info("GUI shown");
	}
}
