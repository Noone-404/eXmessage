package edu.kit.ss17.chatsys.team1.client.Model;

import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;

import java.util.Collection;

/**
 * Holds all messages.
 */
public interface MessageBagInterface {

	void addMessage(MessageInterface message);

	MessageInterface getMessage(String id);
	Collection<MessageInterface> getMessages();
}
