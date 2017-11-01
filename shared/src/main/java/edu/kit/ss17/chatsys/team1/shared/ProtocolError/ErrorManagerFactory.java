package edu.kit.ss17.chatsys.team1.shared.ProtocolError;

import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.Factory;

/**
 * Creates error managers.
 */
public class ErrorManagerFactory implements Factory<ErrorManagerInterface> {

	private static ErrorManagerFactory instance;

	private ErrorManagerFactory() {
	}

	public static ErrorManagerFactory getInstance() {
		return (instance != null) ? instance : (instance = new ErrorManagerFactory());
	}

	public static ErrorManagerInterface make() {
		return getInstance().makeInstance();
	}

	@Override
	public ErrorManagerInterface makeInstance() {
		return new ErrorManager();
	}
}
