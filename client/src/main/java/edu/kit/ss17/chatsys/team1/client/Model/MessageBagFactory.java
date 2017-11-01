package edu.kit.ss17.chatsys.team1.client.Model;

import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ParametrizedBiFactory;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MessageBagFactory implements ParametrizedBiFactory<MessageBagInterface, AccountConfigurationInterface, ContactInterface> {

	private static MessageBagFactory instance;

	private static Map<String, Map<AccountConfigurationInterface, MessageBagInterface>> instances;

	private MessageBagFactory() {
		instances = new HashMap<>();
	}

	public static MessageBagFactory getInstance() {
		return instance != null ? instance : (instance = new MessageBagFactory());
	}

	public static MessageBagInterface getBagFor(AccountConfigurationInterface account, ContactInterface contact) {
		return getInstance().makeInstance(account, contact);
	}

	@Override
	@SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
	public MessageBagInterface makeInstance(AccountConfigurationInterface account, ContactInterface parameter) {
		Map<AccountConfigurationInterface, MessageBagInterface> accountMap = instances.computeIfAbsent(parameter.getJid().getBaseJID(), s -> new HashMap<>());
		return accountMap.computeIfAbsent(account, a -> new MessageBag());
	}
}
