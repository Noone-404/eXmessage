package edu.kit.ss17.chatsys.team1.server.Session;

import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.Factory;

/**
 * Singleton. Creates sessions.
 */
public class SessionFactory implements Factory<SessionInterface> {

	private static SessionFactory   instance;
	private        StorageInterface storage;

	private SessionFactory() {
	}

	public static SessionFactory getInstance() {
		return instance != null ? instance : (instance = new SessionFactory());
	}

	public static SessionInterface make() {
		return SessionFactory.getInstance().makeInstance();
	}

	/**
	 * Sessions will have access to this storage.
	 */
	public void setStorage(StorageInterface storage) {
		this.storage = storage;
	}

	@Override
	public SessionInterface makeInstance() {
		return new Session(this.storage);
	}
}
