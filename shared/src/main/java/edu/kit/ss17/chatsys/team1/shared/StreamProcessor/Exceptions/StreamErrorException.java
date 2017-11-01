package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import org.dom4j.Document;

/**
 * This is the exception to be thrown when an incoming Stream-{@link Document} is malformed.
 */
public class StreamErrorException extends StreamProcessorException {

	private final String               message;
	private final StreamErrorCondition streamErrorCondition;

	/**
	 * @param streamErrorCondition the error condition.
	 * @param message the error message.
	 */
	public StreamErrorException(StreamErrorCondition streamErrorCondition, String message) {
		this.streamErrorCondition = streamErrorCondition;
		this.message = message;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	/**
	 * @return the error condition.
	 */
	public StreamErrorCondition getStreamErrorCondition() {
		return this.streamErrorCondition;
	}
}
