package edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.ErrorElement;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class StreamProtocolErrorTest {

	private StreamProtocolError error;

	@Test
	public void testConstructorMessage() {
		this.error = new StreamProtocolError(StreamErrorCondition.UNDEFINED, "message");
		assertThat("Unexpected error message", this.error.getErrorMessage(), equalTo("message"));
		assertThat("Unexpected isFatal value", this.error.isFatal(), is(true));
		assertThat("Unexpected error condition", this.error.getCondition(), is(StreamErrorCondition.UNDEFINED));
	}

	@Test
	public void testGetCondition() {
		this.error = new StreamProtocolError(StreamErrorCondition.UNDEFINED, "");
		assertThat("Error condition has not the expected value", this.error.getCondition(), is(StreamErrorCondition.UNDEFINED));
	}

	@Test
	public void testMakeErrorElement() {
		this.error = new StreamProtocolError(StreamErrorCondition.UNDEFINED, "");
		ErrorElement element = this.error.makeErrorElement();
		Assert.assertThat("Reference is expected to be of type StreamErrorElement", element.getClass().getSimpleName(), equalTo("StreamErrorElement"));
	}

}