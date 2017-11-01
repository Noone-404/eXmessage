package edu.kit.ss17.chatsys.team1.shared.PluginManager;

import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.Factory;

import java.util.Collection;

/**
 * Singleton. Creates PluginManagers and scans the plugins folder for pluginsets within jar files.
 */
public interface PluginManagerFactoryInterface extends Factory<PluginManagerInterface> {

	/**
	 * Get a list of all found PluginSets.
	 */
	Collection<PluginSetInterface> getPluginSetList();

	/**
	 * Get a list of all activated plugins.
	 */
	Collection<PluginSetInterface> getEnabledPluginSets();

	/**
	 * Tells the factory which storage to use to find getEnabledProperty plugins.
	 */
	void setStorage(StorageInterface storage);


	/**
	 * Scans the plugins directory for jar files, checks each of its classes for implementation of PluginSetInterface and if yes, adds it to our pluginSetList. After that, active
	 * plugin list is retrieved from storage (on client).
	 */
	void scanPlugins();

}
