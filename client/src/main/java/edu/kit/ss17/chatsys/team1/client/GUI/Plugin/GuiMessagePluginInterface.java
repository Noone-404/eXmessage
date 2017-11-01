package edu.kit.ss17.chatsys.team1.client.GUI.Plugin;

import edu.kit.ss17.chatsys.team1.client.Model.MessageBagInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.MessageObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.RosterObserverInterface;

/**
 * Plugin type, which can modify messages at both, the rich text input control and the conversation view.
 */
public interface GuiMessagePluginInterface extends GuiPluginInterface, MessageObserverInterface, RosterObserverInterface {
}
