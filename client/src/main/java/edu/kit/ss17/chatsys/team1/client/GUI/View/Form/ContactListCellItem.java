package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValueInterface;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.jetbrains.annotations.Contract;

import static edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValue.*;

/**
 *
 */
class ContactListCellItem extends ControlBase {

	private static final Color      MEDIUM_LIGHT_GRAY            = new Color(0.91f, 0.91f, 0.91f, 1.0f);
	private static final Color      SELECTION_BLUE               = Color.rgb(0, 150, 201);
	private static final Background SELECTED_ITEM_BACKGROUND     = new Background(new BackgroundFill(SELECTION_BLUE, null, null));
	private static final Background NON_SELECTED_ITEM_BACKGROUND = new Background(new BackgroundFill(Color.TRANSPARENT, null, null));

	@SuppressWarnings("HardCodedStringLiteral")
	private static final Stop[][] CONTACT_STATE_COLORS = {
			{new Stop(0.0f, Color.valueOf("#e02c2c")), new Stop(1.0f, Color.valueOf("#991e00"))}, /* red    */
			{new Stop(0.0f, Color.valueOf("#00a500")), new Stop(1.0f, Color.valueOf("#006600"))}, /* green  */
			{new Stop(0.0f, Color.valueOf("#ffb100")), new Stop(1.0f, Color.valueOf("#ff5d00"))}, /* orange */
			{new Stop(0.0f, Color.valueOf("#808080")), new Stop(1.0f, Color.valueOf("#3f3f3f"))}, /* gray   */
			{new Stop(0.0f, Color.valueOf("#00e500")), new Stop(1.0f, Color.valueOf("#23b223"))}, /* light green */
			{new Stop(0.0f, Color.valueOf("#cc9647")), new Stop(1.0f, Color.valueOf("#a5543a"))}  /* dark orange */
	};

	@FXML
	private GridPane       mainContainer;
	@FXML
	private Label          contactJid;
	@FXML
	private Label          contactAlias;
	@FXML
	private Circle         circle;
	@FXML
	private Pane           circlePanel;
	@FXML
	private RadialGradient gradient;

	private BooleanProperty selectedProperty;
	private boolean         isPersistent;

	public ContactListCellItem(String jid, String alias, boolean persistent, ContactState state) {
		super();

		this.selectedProperty = new SimpleBooleanProperty(false);
		this.selectedProperty.addListener((observable, oldValue, newValue) -> this.setSelected(newValue));

		if (alias != null && !alias.isEmpty()) {
			this.contactAlias.setText(alias);
			this.contactJid.setText(jid == null ? "" : jid);
		} else {
			this.contactAlias.setText(jid == null ? "" : jid);
		}

		this.circlePanel.heightProperty().addListener((observable, oldValue, newValue) -> this.circle.setLayoutY((Double) newValue / 2));
		this.circlePanel.widthProperty().addListener((observable, oldValue, newValue) -> this.circle.setLayoutX((Double) newValue / 2));
		this.isPersistent = persistent;
		setSelected(false);

		setContactState(state);
	}

	public ContactListCellItem(String jid, String alias, boolean persistent) {
		this(jid, alias, persistent, ContactState.GRAY);
	}

	public ContactListCellItem(String jid, String alias, boolean persistent, boolean isDummyElement) {
		this(jid, alias, persistent);

		if (isDummyElement)
			this.mainContainer.setVisible(false);
	}

	private static Font getFont(Font base, boolean isSelected, boolean isPersistent) {
		return Font.font(base.getFamily(), isSelected ? FontWeight.BOLD : FontWeight.NORMAL, isPersistent ? FontPosture.REGULAR : FontPosture.ITALIC, base.getSize());
	}

	private void setSelected(boolean selected) {
		this.contactJid.setTextFill(selected ? MEDIUM_LIGHT_GRAY : Color.GRAY);
		this.contactAlias.setTextFill(selected ? Color.WHITE : Color.BLACK);
		this.mainContainer.setBackground(selected ? SELECTED_ITEM_BACKGROUND : NON_SELECTED_ITEM_BACKGROUND);
		updateFont(selected);
	}

	private void updateFont(boolean selected) {
		Font font = getFont(this.contactJid.getFont(), selected, this.isPersistent);
		this.contactJid.setFont(font);
		this.contactAlias.setFont(font);
	}

	public BooleanProperty getSelectedProperty() {
		return this.selectedProperty;
	}

	public Node getBox() {
		return this.mainContainer;
	}

	public final void setContactState(ContactState state) {
		this.circle.setFill(new RadialGradient(0.0f,
		                                       0.0f,
		                                       0.5f,
		                                       0.5f,
		                                       1.0f,
		                                       true,
		                                       CycleMethod.NO_CYCLE,
		                                       CONTACT_STATE_COLORS[state.getValue()]));
	}

	public enum ContactState {
		RED(0), GREEN(1), ORANGE(2), GRAY(3), LIGHTGREEN(4), DARKORANGE(5);

		private final int value;

		ContactState(int value) {
			this.value = value;
		}

		public static ContactState fromPresence(PresenceValueInterface presence) {
			// use ifs because java's switch-case-statement is pretty useless
			if (presence == DND)
				return RED;
			else if (presence == AWAY)
				return ORANGE;
			else if (presence == XA)
				return DARKORANGE;
			else if (presence == CHAT)
				return LIGHTGREEN;
			else if (presence == ONLINE)
				return GREEN;
			else
				return GRAY;
		}

		@Contract(pure = true)
		int getValue() {
			return this.value;
		}
	}
}
