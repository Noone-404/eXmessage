package edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.client;

import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObserverInterface;

/**
 * Observes the GuiMenuPlugin
 */
public interface GuiMenuPluginObserverInterface extends ObserverInterface {

	/**
	 * GuiMenuPlugin requests a byte sending/receiving test.
	 *
	 * @param amount the amount of bytes.
	 */
	void startByteTest(int amount);
}
