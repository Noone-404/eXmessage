package edu.kit.ss17.chatsys.team1.shared.PluginManager;

import edu.kit.ss17.chatsys.team1.shared.Network.NetworkProtocolBase.NetworkProtocolPluginInterface;
import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 * Singleton. Creates PluginManagers and scans the plugins folder for pluginsets within jar files.
 */
public class PluginManagerFactory implements PluginManagerFactoryInterface {

	private static final Logger logger            = LogManager.getLogger(APP_NAME);
	private static final String PLUGINS_DIRECTORY = "plugins";

	private static PluginManagerFactory instance;
	private static Class                root;

	private final Map<String, Class<?>>          pluginSetClasses;
	private final Collection<PluginSetInterface> globalPluginSets;
	private       Collection<String>             enabledPluginSets;
	private       StorageInterface               storage;
	private       boolean                        pluginsRead;
	private       Collection<URLClassLoader>     loaders;

	private PluginManagerFactory() {
		this.pluginSetClasses = new HashMap<>();
		this.globalPluginSets = new ArrayList<>();
		this.enabledPluginSets = new ArrayList<>();
		this.loaders = new ArrayList<>();
	}

	public static PluginManagerFactory getInstance() {
		return instance != null ? instance : (instance = new PluginManagerFactory());
	}

	public static PluginManagerInterface make() {
		return getInstance().makeInstance();
	}

	private static Class getRoot() {
		return root == null ? (root = PluginManagerFactory.class) : root;
	}

	public static void setRoot(Class newRoot) {
		root = newRoot;
	}

	private static String getPluginsDirectory() {
		File basePath;
		try {
			basePath = Paths.get(getRoot().getProtectionDomain().getCodeSource().getLocation().toURI()).toFile();
		} catch (URISyntaxException ignored) {
			basePath = new File(PluginManagerFactory.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		}

		String path = basePath.isDirectory() ? basePath.getPath() : basePath.getParent();
		return Paths.get(path, PLUGINS_DIRECTORY).toFile().getPath();
	}

	@Override
	public PluginManagerInterface makeInstance() {
		if (!this.pluginsRead)
			scanPlugins();

		// Create new instances of all enabled PluginSets.
		Collection<PluginSetInterface> enabled = new ArrayList<>();
		for (String name : this.enabledPluginSets) {
			try {
				PluginSetInterface psi = (PluginSetInterface) this.pluginSetClasses.get(name).getConstructor().newInstance();
				psi.setEnabled(true);
				psi.enablePlugins();
				enabled.add(psi);
			} catch (Exception e) {
				logger.warn("Failed to create plugin set " + name + ": " + e.getMessage());
			}
		}

		// Return new PluginManager with our new instances.
		return new PluginManager(enabled);
	}

	void unload() {
		this.loaders.forEach(loader -> {
			try {
				loader.close();
			} catch (IOException e) {
				logger.warn("Failed to unload classLoader: " + e.getMessage());
			}
		});
	}

	@Override
	public void scanPlugins() {
		this.pluginSetClasses.clear();
		this.enabledPluginSets.clear();
		this.globalPluginSets.clear();

		// On Server (no storage given): all pluginsets are getEnabledProperty
		// On Client: check storage to retrieve getEnabledProperty pluginsets
		Path dir = Paths.get(getPluginsDirectory());


		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.jar")) {

			for (Path path : stream) {
				File jar = new File(path.toString());

				logger.debug("Found file: '" + path.toAbsolutePath().toString() + '\'');

				URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {jar.toURI().toURL()});
				this.loaders.add(classLoader);

				try (FileInputStream fis = new FileInputStream(jar)) {
					try (JarInputStream is = new JarInputStream(fis)) {
						JarEntry entry;
						while ((entry = is.getNextJarEntry()) != null) {
							if (entry.getName().endsWith(".class") && !entry.getName().contains("$")) {

								try {
									String classname = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);

									Class<?> pluginSetCandidate = Class.forName(classname, true, classLoader);
									if (PluginSetInterface.class.isAssignableFrom(pluginSetCandidate)) {
										// Bingo! This class implements PluginSetInterface!
										PluginSetInterface psi = (PluginSetInterface) pluginSetCandidate.getConstructor().newInstance();
										this.globalPluginSets.add(psi);
										this.pluginSetClasses.put(psi.getName(), pluginSetCandidate);
										logger.debug("Loading pluginset '" + psi.getName() + '\'');
									}
								} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
									logger.debug("Skipping failing pluginset in '" + jar.getName() + "' (" + e.getMessage() + ')');
								}

							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.debug("An exception occured while loading plugin sets: " + e.getMessage());
			return;
		}

		// Bind a listener to our global PluginSets status
		for (PluginSetInterface psi : this.globalPluginSets) {
			psi.getEnabledProperty().addListener((observable, oldValue, newValue) -> {
				if (oldValue && !newValue) {
					disablePluginSet(psi);
					savePluginSetsStatus();
				} else if (!oldValue && newValue) {
					enablePluginSet(psi);
					savePluginSetsStatus();
				}
			});
		}

		// Add Pluginsets to enabled if it's configured like that.
		if (this.storage != null) {
			Collection<String> activePluginSets = this.storage.getActivePluginSets();

			for (PluginSetInterface psi : this.globalPluginSets)
				if (activePluginSets.contains(psi.getName()) ||
				    psi.getPlugins().stream().anyMatch(pluginInterface -> pluginInterface instanceof NetworkProtocolPluginInterface)) // Enable NetworkProtocolPlugins by default
					enablePluginSet(psi);

		} else // All Pluginsets enabled.
			for (PluginSetInterface psi : this.globalPluginSets)
				enablePluginSet(psi);

		this.pluginsRead = true;
	}

	@Override
	public Collection<PluginSetInterface> getPluginSetList() {
		return Collections.unmodifiableCollection(this.globalPluginSets);
	}

	@Override
	public Collection<PluginSetInterface> getEnabledPluginSets() {
		Collection<PluginSetInterface> enabled = new ArrayList<>();
		for (PluginSetInterface psi : this.globalPluginSets) {
			if (this.enabledPluginSets.contains(psi.getName()))
				enabled.add(psi);
		}
		return Collections.unmodifiableCollection(enabled);
	}

	/**
	 * Enables a PluginSet.
	 */
	void enablePluginSet(PluginSetInterface pluginSet) {
		if (!this.enabledPluginSets.contains(pluginSet.getName())) {
			this.enabledPluginSets.add(pluginSet.getName());
			pluginSet.setEnabled(true);
			pluginSet.enablePlugins();
		}
	}

	/**
	 * Disables a PluginSet.
	 */
	void disablePluginSet(PluginSetInterface pluginSet) {
		if (this.enabledPluginSets.contains(pluginSet.getName())) {
			this.enabledPluginSets.remove(pluginSet.getName());
			pluginSet.setEnabled(false);
		}
	}

	/**
	 * Stores the current set of enabled PluginSets to storage.
	 */
	private void savePluginSetsStatus() {
		if (this.storage != null) {
			Collection<PluginSetInterface> enabled = new ArrayList<>();
			for (PluginSetInterface psi : this.globalPluginSets) {
				if (this.enabledPluginSets.contains(psi.getName()))
					enabled.add(psi);
			}
			this.storage.setActivePluginsSets(enabled);
		}
	}

	@Override
	public void setStorage(StorageInterface storage) {
		this.storage = storage;
	}

}
