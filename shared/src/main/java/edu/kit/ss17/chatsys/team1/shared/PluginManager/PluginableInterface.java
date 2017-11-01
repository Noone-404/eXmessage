package edu.kit.ss17.chatsys.team1.shared.PluginManager;

/**
 * Interface with plugin support.
 */
@FunctionalInterface
public interface PluginableInterface {

	void tryRegisterPlugin(PluginInterface plugin, boolean lastPlugin);
}
