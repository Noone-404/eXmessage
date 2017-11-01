package edu.kit.ss17.chatsys.team1.shared.ProtocolTranslator;

import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.Factory;

/**
 * Singleton. Creates NetworkProtocolBase Translators.
 */
public class ProtocolTranslatorFactory implements Factory<ProtocolTranslatorInterface> {

	private static ProtocolTranslatorFactory instance;

	private ProtocolTranslatorFactory() {
	}

	public static ProtocolTranslatorFactory getInstance() {
		return (instance != null) ? instance : (instance = new ProtocolTranslatorFactory());
	}

	public static ProtocolTranslatorInterface make() {
		return getInstance().makeInstance();
	}

	@Override
	public ProtocolTranslatorInterface makeInstance() {
		return new ProtocolTranslator();
	}
}
