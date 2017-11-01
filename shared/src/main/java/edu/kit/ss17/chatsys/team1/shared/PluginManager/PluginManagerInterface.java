package edu.kit.ss17.chatsys.team1.shared.PluginManager;

import java.util.Collection;

/**
 *
 */
public interface PluginManagerInterface {

	Collection<PluginSetInterface> getPluginSets();

	/**
	 * Register a @code{Pluginable} implementation, so that this class knows where to register its @code{PluginSet}s.
	 */
	void registerPluginable(PluginableInterface pluginable);

	/**
	 * Start registering all PluginSets at registered @code{Pluginable} implementations.
	 */
	void registerPlugins();
}
