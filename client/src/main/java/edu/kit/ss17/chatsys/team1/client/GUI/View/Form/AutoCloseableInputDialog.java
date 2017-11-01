package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.HashMap;

/**
 *
 */
class AutoCloseableInputDialog extends javafx.scene.control.TextInputDialog implements AutoCloseableFormInterface {

	private static Image                                                                  icon;
	private        HashMap<AutoCloseableFormObserverInterface, EventHandler<DialogEvent>> observers;

	AutoCloseableInputDialog(String title, String caption, String content) {
		this(title, caption, content, "");
	}

	AutoCloseableInputDialog(String title, String caption, String content, String defaultValue) {
		super(defaultValue);
		this.setTitle(title);
		this.setHeaderText(caption);
		this.setContentText(content);
		DialogPane dialogPane = this.getDialogPane();
		((Stage) dialogPane.getScene().getWindow()).getIcons().add(getIcon());

		dialogPane.getButtonTypes().stream()
		          .map(dialogPane::lookupButton)
		          .forEach(button -> button.addEventHandler(KeyEvent.KEY_PRESSED, e2 -> {
			          if (KeyCode.ENTER.equals(e2.getCode()) && e2.getTarget() instanceof Button)
				          ((Button) e2.getTarget()).fire();
		          }));


		dialogPane.getScene().getWindow().setOnShown(event -> getEditor().requestFocus());
		this.observers = new HashMap<>();
	}

	static void setDefaultIcon(Image icon) {
		AutoCloseableInputDialog.icon = icon;
	}

	private static Image getIcon() {
		return icon == null ? new WritableImage(16, 16) : icon; // empty image if no icon set
	}

	@Override
	public void registerObserver(AutoCloseableFormObserverInterface observer) {
		this.getDialogPane().addEventHandler(DialogEvent.DIALOG_HIDDEN, getCloseEventHandler(observer));
	}

	@Override
	public void unregisterObserver(AutoCloseableFormObserverInterface observer) {
		this.getDialogPane().removeEventHandler(DialogEvent.DIALOG_HIDDEN, getCloseEventHandler(observer));
	}

	@SuppressWarnings("SynchronizedMethod")
	private synchronized EventHandler<DialogEvent> getCloseEventHandler(AutoCloseableFormObserverInterface observer) {
		return this.observers.computeIfAbsent(observer, o -> event -> o.closed());
	}
}
