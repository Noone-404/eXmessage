package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ResourceBundle;

/**
 *
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods") // direct instantiation of this class is useless and will fail anyway
public abstract class ControlBase {

	private static final String VIEW_RESOURCE_EXTENSION = ".fxml";

	private static ResourceBundle localeResource;
	private        Scene          scene;

	protected ControlBase() {
		// note: in order to prevent infinitive recursion, the fxml file must not specify a controller via fx:controller attribute.
		// If a controller is specified, the FXMLLoader (in loadParent()) will attempt to instantiate the controller class, leading to another invocation of loadParent() and so on
		try {
			this.scene = new Scene(loadParent());
		} catch (IOException e) {
			throw new UncheckedIOException(e); // client code cannot do anything if loading of resource failed
		}
	}

	public static void setLocaleResource(ResourceBundle locale) {
		localeResource = locale;
	}

	protected static ResourceBundle getLocaleResource() {
		return localeResource;
	}

	protected Scene getScene() {
		return this.scene;
	}

	private Parent loadParent() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(getClass().getSimpleName() + VIEW_RESOURCE_EXTENSION));

		if (localeResource != null)
			loader.setResources(localeResource);

		loader.setController(this); // needed as we did not specify a controller in the fxml file to prevent recursion. Alternative: dont call loadParent() in the constructor

		return loader.load();
	}
}
