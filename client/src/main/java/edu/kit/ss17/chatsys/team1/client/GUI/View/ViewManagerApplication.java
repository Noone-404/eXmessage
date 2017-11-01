package edu.kit.ss17.chatsys.team1.client.GUI.View;

import edu.kit.ss17.chatsys.team1.client.GUI.View.Form.MainForm;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 * JavaFX application entry point.
 */
public class ViewManagerApplication extends Application {

	private static final Logger logger = LogManager.getLogger(APP_NAME);

	private static ApplicationViewManagerInterface viewManager;

	public static void setAssociatedViewManager(ApplicationViewManagerInterface viewManager) {
		ViewManagerApplication.viewManager = viewManager;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		logger.debug("FX application started");

		if (viewManager == null)
			throw new IllegalStateException("ViewManager not initialized");

		viewManager.performStaticInitialisation();

		// create / register / init main form
		viewManager.performInitialisation(new MainForm(primaryStage));
	}

	@Override
	public void stop() {
		if (viewManager != null && viewManager.getLastFormClosedConsumer() != null)
			viewManager.getLastFormClosedConsumer().accept(null);
	}
}
