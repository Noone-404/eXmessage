package edu.kit.ss17.chatsys.team1.shared.Storage;

import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Defines methods for file storage operations.
 */
public interface FileStorageInterface {

	/**
	 * Save the clients language.
	 */
	default void saveLanguage(String lang) {
	}

	/**
	 * Get the clients language.
	 */
	default String getLanguage() {
		return null;
	}

	/**
	 * Get a simple account with jid and password.
	 */
	@Nullable
	AccountInterface getAccount(JID jid);

	/**
	 * Gets all known JIDs
	 */
	default Collection<JID> getAllAccountJids() {
		return null;
	}
}
