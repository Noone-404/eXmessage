package edu.kit.ss17.chatsys.team1.shared.ProtocolError;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.StreamProcessorInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObservableInterface;

/**
 * Manages Error objects inside a {@Link ConnectionStack}.
 */
public interface ErrorManagerInterface extends ObservableInterface<ErrorManagerObserverInterface> {

	/**
	 * Setter for a reference to the current {@Link StreamProcessorInterface} implementation.
	 *
	 * @param processor the {@Link StreamProcessorInterface} implementation to set.
	 */
	void setStreamProcessor(StreamProcessorInterface processor);

	/**
	 * Registers a new {@Link ProtocolErrorObserverInterface} implementation at the specified component.
	 *
	 * @param component the {@Link ProtocolErrorObservableInterface} instance at which a new observer shall be registered.
	 */
	void registerObserverAt(ProtocolErrorObservableInterface component);
}
