package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import com.sun.glass.ui.Size;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.List;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 *
 */
abstract class FormBase extends ControlBase implements FormBaseInterface {

	@NonNls
	protected String iconName = "icon.png";

	private Stage stage;
	private Image icon;

	private static void addAllDescendants(Parent parent, ArrayList<Node> nodes) {
		for (Node node : parent.getChildrenUnmodifiable()) {
			nodes.add(node);
			if (node instanceof Parent)
				addAllDescendants((Parent) node, nodes);
		}
	}

	@Override
	public List<Node> getAllNodes() {
		ArrayList<Node> nodes = new ArrayList<>();
		addAllDescendants(getStage().getScene().getRoot(), nodes);
		return nodes;
	}

	protected Stage getStage() {
		if (this.stage == null)
			setStage(new Stage());

		return this.stage;
	}

	protected void setStage(Stage stage) {
		stage.setScene(getScene());
		if (getIcon() != null)
			stage.getIcons().add(getIcon());
		if (getDefaultSize() != null) {
			Size size = getDefaultSize();
			stage.setMinWidth(size.width);
			stage.setWidth(size.width);
			stage.setMinHeight(size.height);
			stage.setHeight(size.height);
		}
		if (getTitle() != null)
			stage.setTitle(getTitle());

		this.stage = stage;
	}

	@Override
	public void show() {
		getStage().show();
		getStage().toFront();
		getAllNodes().stream().filter(node -> node instanceof Button).forEach(button -> button.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			if (KeyCode.ENTER == e.getCode() && e.getTarget() instanceof Button)
				((ButtonBase) e.getTarget()).fire();
		}));
	}

	protected Image getIcon() {
		return this.icon == null ? (this.icon = new Image(getClass().getResourceAsStream(this.iconName))) : this.icon;
	}

	protected String getTitle() {
		return APP_NAME;
	}

	protected abstract Size getDefaultSize();
}
