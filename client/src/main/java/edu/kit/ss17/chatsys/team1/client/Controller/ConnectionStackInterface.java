package edu.kit.ss17.chatsys.team1.client.Controller;

import edu.kit.ss17.chatsys.team1.shared.ConnectionStack.ConnectionStackBaseInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountConfigurationInterface;

/**
 * The client connection stack.
 */
public interface ConnectionStackInterface extends ConnectionStackBaseInterface {

	/**
	 * Get the account configuration object which identifies this connection stack.
	 */
	AccountConfigurationInterface getAccountConfiguration();

}
