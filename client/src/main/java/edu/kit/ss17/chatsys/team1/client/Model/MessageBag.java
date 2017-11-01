package edu.kit.ss17.chatsys.team1.client.Model;

import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 *
 */
public class MessageBag implements MessageBagInterface {

	private static final Logger logger = LogManager.getLogger(APP_NAME);

	private Map<String, MessageInterface> messages = new HashMap<>();

	MessageBag() {
	}

	@Override
	public void addMessage(MessageInterface message) {
		logger.trace("MessageBag: adding message " + message.getID());
		this.messages.put(message.getID(), message);
	}

	@Override
	public MessageInterface getMessage(String id) {
		return this.messages.get(id);
	}

	@Override
	public Collection<MessageInterface> getMessages() {
		return new ArrayList<>(this.messages.values());
	}
}
