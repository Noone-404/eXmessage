package edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class AbstractProtocolErrorTest {

	private AbstractProtocolError error;

	@Test
	public void testGetErrorMessage() {
		String testString = "test";
		this.error = new StreamProtocolError(null, testString); // TODO do something else
		assertThat("Error message is not the expected object", this.error.getErrorMessage(), is(testString));
	}

	@Test
	public void testIsFatal() {
		this.error = new StreamProtocolError(null, null); // TODO do something else
		assertThat("isFatal has not the expected value", this.error.isFatal(), is(true));
	}

}