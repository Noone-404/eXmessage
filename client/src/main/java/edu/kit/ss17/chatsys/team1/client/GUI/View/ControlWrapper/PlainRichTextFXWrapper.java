package edu.kit.ss17.chatsys.team1.client.GUI.View.ControlWrapper;

import edu.kit.ss17.chatsys.team1.client.Model.PlainTextContentFragment;
import edu.kit.ss17.chatsys.team1.client.Model.TextContentFragmentInterface;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;


/**
 * Wrapper for RichTextFX.
 */
public class PlainRichTextFXWrapper implements TextInputWrapperInterface<Node> {

	private Node                 control;
	private StyleClassedTextArea area;

	private Property<TextContentFragmentInterface> contentFragmentProperty;

	public PlainRichTextFXWrapper() {
		this.area = new StyleClassedTextArea();
		this.area.setWrapText(true);
		this.area.disableProperty().addListener(
				(observable, oldValue, newValue) -> this.area.setBackground(new Background(new BackgroundFill(newValue ? Color.grayRgb(244) : Color.WHITE, null, null))));

		this.control = new ScrollPane(this.area);

		this.contentFragmentProperty = new SimpleObjectProperty<>(new PlainTextContentFragment(this.area.getText()));
		this.contentFragmentProperty.addListener((observable, oldValue, newValue) -> this.area.replaceText(newValue == null ? "" : newValue.getPlaintextRepresentation()));
		this.area.textProperty().addListener((observable, oldValue, newValue) -> this.contentFragmentProperty.setValue(new PlainTextContentFragment(newValue)));
	}

	@Override
	public Node getControl() {
		return this.control;
	}

	@Override
	public IndexRange getSelection() {
		return this.area.getSelection();
	}

	@Override
	public Property<TextContentFragmentInterface> textContentProperty() {
		return this.contentFragmentProperty;
	}

	@Override
	public void clear() {
		this.area.clear();
	}

	private static class ScrollPane extends VirtualizedScrollPane<StyleClassedTextArea> {

		private StyleClassedTextArea content;

		public ScrollPane(StyleClassedTextArea content, ScrollBarPolicy hPolicy, ScrollBarPolicy vPolicy) {
			super(content, hPolicy, vPolicy);
			content.disableProperty().bind(disabledProperty());
			this.content = content;
		}

		public ScrollPane(StyleClassedTextArea content) {
			super(content);
			content.disableProperty().bind(disabledProperty());
			this.content = content;
		}

		@Override
		public void requestFocus() {
			this.content.requestFocus();
		}
	}
}
