package edu.kit.ss17.chatsys.team1.client.Session;

import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.Factory;

/**
 * Singleton. Creates sessions.
 */
public class SessionFactory implements Factory<SessionInterface> {

	private static SessionFactory instance;

	private SessionFactory() {
	}

	public static SessionFactory getInstance() {
		return instance != null ? instance : (instance = new SessionFactory());
	}

	public static SessionInterface make() {
		return SessionFactory.getInstance().makeInstance();
	}

	@Override
	public SessionInterface makeInstance() {
		return new Session();
	}
}
