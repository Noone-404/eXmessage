package edu.kit.ss17.chatsys.team1.client.GUI.Internationalization;

import java.util.ResourceBundle;

/**
 *
 */
public interface LocaleInterface {

	/**
	 * Gets the corresponding resource bundle for this locale.
	 *
	 * @return A resource bundle for this locale. If the locale cannot be found, the default locale will be used.
	 */
	ResourceBundle getBundle();

	/**
	 * Gets the current language tag.
	 *
	 * @return The identifier of this locale.
	 */
	String getIdentifier();

	/**
	 * Gets a human-readable name for this locale.
	 *
	 * @return A human-readable name for this locale.
	 */
	String getDisplayName();

	/**
	 * Marks this locale as currently chosen.
	 */
	void makeCurrent();

	/**
	 * Checks if this locale is currently selected.
	 *
	 * @return Returns true if this locale is the currently chosen one.
	 */
	boolean isCurrent();
}
