package edu.kit.ss17.chatsys.team1.client.GUI.View;

import edu.kit.ss17.chatsys.team1.client.GUI.View.Form.MainFormInterface;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Used to communicate between ViewManager and the JavaFX-Application
 */
interface ApplicationViewManagerInterface {

	/**
	 * Will be called at JavaFX application initialisation from different thread befor any forms are instantiated.
	 */
	void performStaticInitialisation();

	/**
	 * Will be called at JavaFX application initialisation from different thread.
	 *
	 * @param mainForm The new MainForm
	 */
	void performInitialisation(MainFormInterface mainForm);

	/**
	 * Gets a void-consumer (callback) that will be called when the last form is closed
	 *
	 * @return Returns a consumer to be called when the last form is closed
	 */
	@Nullable
	Consumer<Void> getLastFormClosedConsumer();
}
