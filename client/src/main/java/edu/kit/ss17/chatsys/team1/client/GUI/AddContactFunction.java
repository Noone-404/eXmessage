package edu.kit.ss17.chatsys.team1.client.GUI;

import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;

/**
 *
 */
@FunctionalInterface
public interface AddContactFunction {

	void accept(String contact) throws InvalidJIDException;
}
