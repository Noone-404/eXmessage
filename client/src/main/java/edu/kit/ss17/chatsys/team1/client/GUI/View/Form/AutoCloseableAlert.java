package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.HashMap;

/**
 *
 */
class AutoCloseableAlert extends javafx.scene.control.Alert implements AutoCloseableFormInterface {

	private static Image                                                                  icon;
	private        HashMap<AutoCloseableFormObserverInterface, EventHandler<DialogEvent>> observers;
	private boolean manualCloseProhibited = true;

	AutoCloseableAlert(AlertType type, String title, String caption, String text, ButtonType defaultButton, ButtonType... buttons) {
		super(type, text, buttons.length == 0 && defaultButton != null ? new ButtonType[] {defaultButton} : buttons);
		this.setTitle(title);
		this.setHeaderText(caption);
		DialogPane dialogPane = this.getDialogPane();
		((Stage) dialogPane.getScene().getWindow()).getIcons().add(getIcon());
		dialogPane.getButtonTypes().stream()
		          .map(dialogPane::lookupButton)
		          .peek(button -> ((Button) button).setDefaultButton(false))
		          .forEach(button -> button.addEventHandler(KeyEvent.KEY_PRESSED, e2 -> {
			          if (KeyCode.ENTER.equals(e2.getCode()) && e2.getTarget() instanceof Button)
				          ((Button) e2.getTarget()).fire();
		          }));
		if (defaultButton != null)
			((Button) dialogPane.lookupButton(defaultButton)).setDefaultButton(true);
		else if (buttons.length == 0)
			dialogPane.getButtonTypes().stream().map(dialogPane::lookupButton).forEach(button -> button.setManaged(false));


		dialogPane.getScene().getWindow().setOnCloseRequest(event -> {
			if (this.manualCloseProhibited)
				event.consume();
		});

		this.observers = new HashMap<>();
	}

	static void setDefaultIcon(Image icon) {
		AutoCloseableAlert.icon = icon;
	}

	private static Image getIcon() {
		return icon == null ? new WritableImage(16, 16) : icon; // empty image if no icon set
	}

	void setManualCloseProhibited(boolean value) {
		this.manualCloseProhibited = value;
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
