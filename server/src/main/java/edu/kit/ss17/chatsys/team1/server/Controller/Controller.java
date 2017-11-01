package edu.kit.ss17.chatsys.team1.server.Controller;

import edu.kit.ss17.chatsys.team1.server.Router.Router;
import edu.kit.ss17.chatsys.team1.server.Session.SessionFactory;
import edu.kit.ss17.chatsys.team1.server.Storage.Storage;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement.NetworkStackFactory;
import edu.kit.ss17.chatsys.team1.shared.Network.NetworkManagement.NetworkStackInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginManagerFactory;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginableInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 * Default Server Controller.
 */
public class Controller implements ControllerInterface {

	private static final Logger logger = LogManager.getLogger(APP_NAME);
	private static Controller instance;
	private static Collection<ConnectionStackInterface> connectionStacks = new ArrayList<>();

	private Controller() {
		AnsiConsole.systemInstall();

		SessionFactory.getInstance().setStorage(Storage.getInstance());
		Router.getInstance().setStorage(Storage.getInstance());
		NetworkStackFactory.getInstance().registerObserver(Controller::createNewStack);
		PluginManagerFactory.setRoot(Controller.class);
		registerPlugins();
		NetworkStackFactory.getInstance().startListening();
	}

	public static Controller getInstance() {
		return instance != null ? instance : (instance = new Controller());
	}

	private static void createNewStack(NetworkStackInterface networkStack) {
		logger.info("Creating new ConnectionStack");
		ConnectionStackInterface stack;
		try {
			stack = new ConnectionStack(networkStack);
		} catch (IOException e) {
			logger.error("Failed to establish connection: " + e.getMessage());
			return;
		}
		stack.setRouter(Router.getInstance());
		connectionStacks.add(stack);
		// Connection stack will register itself at the router once its user is fully authenticated.
	}

	/**
	 * Registers plugins at the NetworkStackFactory.
	 */
	private static void registerPlugins() {
		PluginManagerFactory.getInstance().scanPlugins();
		List<PluginableInterface> tmpPluginables = Collections.singletonList(NetworkStackFactory.getInstance());

		List<PluginSetInterface> sets    = new ArrayList<>(PluginManagerFactory.getInstance().getEnabledPluginSets());
		int                      numSets = sets.size();

		for (int i = 0; i < numSets; i++) {
			boolean lastSet = (i + 1) == numSets;

			PluginSetInterface    psi        = sets.get(i);
			List<PluginInterface> plugins    = new ArrayList<>(psi.getPlugins());
			int                   numPlugins = plugins.size();

			for (int j = 0; j < numPlugins; j++) {
				boolean lastPlugin = (j + 1) == numPlugins;

				for (PluginableInterface pluginable : tmpPluginables) {
					logger.info("Registering plugin '" + plugins.get(j).getClass().getSimpleName() + "' at pluginable '" + pluginable.getClass().getSimpleName() + '\'');
					pluginable.tryRegisterPlugin(plugins.get(j), lastSet && lastPlugin);
				}
			}
		}
	}

	@Override
	public void closeConnectionStack(ConnectionStackInterface stack) {
		logger.info("Closing ConnectionStack");
		connectionStacks.remove(stack);

	}
}
