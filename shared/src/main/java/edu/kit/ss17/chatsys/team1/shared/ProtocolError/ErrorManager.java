package edu.kit.ss17.chatsys.team1.shared.ProtocolError;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.ErrorElement;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.StreamProcessorInterface;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Default implementation of {@Link ErrorManagerInterface}.
 */
public class ErrorManager implements ErrorManagerInterface {

	private StreamProcessorInterface                  streamProcessor;
	private Collection<ErrorManagerObserverInterface> observers;

	public ErrorManager() {
		this.streamProcessor = null;
		this.observers = new ArrayList<>();
	}

	@Override
	public void setStreamProcessor(StreamProcessorInterface processor) {
		this.streamProcessor = processor;
	}

	@Override
	public void registerObserverAt(ProtocolErrorObservableInterface component) {
		component.registerErrorObserver(error -> {
			ErrorElement errorXML = error.makeErrorElement();
			this.streamProcessor.pushDataDown(errorXML);
			notifyObservers(error);
		});
	}

	@Override
	public void registerObserver(ErrorManagerObserverInterface observer) {
		if (!this.observers.contains(observer)) {
			this.observers.add(observer);
		}
	}

	@Override
	public void unregisterObserver(ErrorManagerObserverInterface observer) {
		if (this.observers.contains(observer)) {
			this.observers.remove(observer);
		}
	}

	Collection<ErrorManagerObserverInterface> getObservers() {
		return this.observers;
	}

	StreamProcessorInterface getStreamProcessor() {
		return this.streamProcessor;
	}

	private void notifyObservers(ProtocolErrorInterface error) {
		this.observers.forEach(observer -> observer.errorReceived(error));
	}

}
