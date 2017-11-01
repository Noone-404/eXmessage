package edu.kit.ss17.chatsys.team1.shared.PluginManager;

import javafx.beans.property.BooleanProperty;

import java.util.Collection;

/**
 * Describes a set of internal plugins which are all together exposed to the user as one plugin. E.g., a conference plugin might include a stanza plugin as well as a session
 * plugin.
 */
public interface PluginSetInterface extends Cloneable {

	/**
	 * Get the name of the plugin set, e.g., "Image Messages".
	 */
	String getName();

	/**
	 * Get the description of the plugin set.
	 */
	String getDescription();

	/**
	 * Get the version string of the plugin set.
	 */
	String getVersion();

	/**
	 * Get a collection of component plugins within the plugin set.
	 */
	Collection<PluginInterface> getPlugins();

	/**
	 * Gets the current enable state of the plugin set as boolean property.
	 */
	BooleanProperty getEnabledProperty();

	/**
	 * Sets the state of this plugin set.
	 *
	 * @param enabled the state
	 */
	void setEnabled(boolean enabled);

	/**
	 * Activate all plugins of this set. Usually called by a negotiator/session plugin after successful negotiation.
	 */
	void enablePlugins();
}
