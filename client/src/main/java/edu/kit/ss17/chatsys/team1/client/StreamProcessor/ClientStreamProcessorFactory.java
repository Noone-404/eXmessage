package edu.kit.ss17.chatsys.team1.client.StreamProcessor;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.StreamProcessorInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.Factory;

/**
 * Singleton. Creates StreamProcessorInterface objects.
 */
public final class ClientStreamProcessorFactory implements Factory<StreamProcessorInterface> {

	private static ClientStreamProcessorFactory instance;

	/**
	 * Private constructor because it is a singleton.
	 */
	private ClientStreamProcessorFactory() {
		ClientStreamProcessor.setUp(); // set up StreamProcessor class.
	}

	/**
	 * @return this Singleton's instance.
	 */
	public static ClientStreamProcessorFactory getInstance() {
		return (instance != null) ? instance : (instance = new ClientStreamProcessorFactory());
	}

	/**
	 * @return a new StreamProcessor instance.
	 */
	public static StreamProcessorInterface make() {
		return getInstance().makeInstance();
	}

	@Override
	public StreamProcessorInterface makeInstance() {
		return new ClientStreamProcessor();
	}
}
