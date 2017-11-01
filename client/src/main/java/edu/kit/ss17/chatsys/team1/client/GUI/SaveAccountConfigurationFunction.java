package edu.kit.ss17.chatsys.team1.client.GUI;

import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;

/**
 *
 */
@FunctionalInterface
public interface SaveAccountConfigurationFunction {

	void accept(AccountConfigurationInterface accountConfiguration) throws InvalidJIDException;
}
