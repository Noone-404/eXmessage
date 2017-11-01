package edu.kit.ss17.chatsys.team1.shared.Util;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 *
 */
public interface StyleInterface {

	default <T extends Event> void addEventHandler(final EventType<T> eventType, final EventHandler<? super T> eventHandler) {
	}
	default <T extends Event> void removeEventHandler(final EventType<T> eventType, final EventHandler<? super T> eventHandler) {
	}

	default Cursor getCursor() {
		return null;
	}
	default void setCursor(Cursor value) {
	}

	default double getX() {
		return 0;
	}
	default void setX(double value) {
	}

	default double getY() {
		return 0;
	}
	default void setY(double value) {
	}

	default Font getFont() {
		return null;
	}
	default void setFont(Font value) { // Bold, Italic & Size can be set by accordingly specifying font
	}

	default VPos getTextOrigin() {
		return null;
	}
	default void setTextOrigin(VPos value) {
	}

	default boolean isUnderline() {
		return false;
	}
	default void setUnderline(boolean value) {
	}

	default boolean isStrikethrough() {
		return false;
	}
	default void setStrikethrough(boolean value) {
	}

	default TextAlignment getTextAlignment() {
		return null;
	}
	default void setTextAlignment(TextAlignment value) {
	}

	default double getLineSpacing() {
		return 0;
	}
	default void setLineSpacing(double spacing) {
	}
}
