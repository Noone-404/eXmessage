package edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorType;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.ErrorElement;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 */
public class StanzaProtocolErrorTest {

	private StanzaProtocolError error;

	@Test
	public void testConstructorMessage() {
		this.error = new StanzaProtocolError("", "","", "test", StanzaErrorType.CANCEL, StanzaErrorCondition.UNDEFINED, "message", false);
		assertThat("Unexpected error message", this.error.getErrorMessage(), equalTo("message"));
		assertThat("Unexpected isFatal value", this.error.isFatal(), is(false));
		assertThat("Unexpected stanza type", this.error.getStanzaID(), equalTo("test"));
		assertThat("Unexpected error type", this.error.getErrorType(), is(StanzaErrorType.CANCEL));
		assertThat("Unexpected error condition", this.error.getCondition(), is(StanzaErrorCondition.UNDEFINED));
	}

	@Test
	public void testConstructorBoolean() {
		this.error = new StanzaProtocolError("", "", "", "test", StanzaErrorType.CANCEL, StanzaErrorCondition.UNDEFINED, "", true);
		assertThat("Unexpected error message", this.error.getErrorMessage(), equalTo(""));
		assertThat("Unexpected isFatal value", this.error.isFatal(), is(true));
		assertThat("Unexpected stanza type", this.error.getStanzaID(), equalTo("test"));
		assertThat("Unexpected error type", this.error.getErrorType(), is(StanzaErrorType.CANCEL));
		assertThat("Unexpected error condition", this.error.getCondition(), is(StanzaErrorCondition.UNDEFINED));
	}

	@Test
	public void testConstructorMessageAndBoolean() {
		this.error = new StanzaProtocolError("", "", "", "test", StanzaErrorType.CANCEL, StanzaErrorCondition.UNDEFINED, "message", true);
		assertThat("Unexpected error message", this.error.getErrorMessage(), equalTo("message"));
		assertThat("Unexpected isFatal value", this.error.isFatal(), is(true));
		assertThat("Unexpected stanza type", this.error.getStanzaID(), equalTo("test"));
		assertThat("Unexpected error type", this.error.getErrorType(), is(StanzaErrorType.CANCEL));
		assertThat("Unexpected error condition", this.error.getCondition(), is(StanzaErrorCondition.UNDEFINED));
	}

	@Before
	public void setUp() {
		this.error = new StanzaProtocolError("", "", "", "test", StanzaErrorType.CANCEL, StanzaErrorCondition.UNDEFINED, "", false);
	}

	@Test
	public void testGetStanzaType() {
		assertThat("Stanza id has not the expected value", error.getStanzaID(), equalTo("test"));
	}

	@Test
	public void testGetErrorType() {
		assertThat("Error type has not the expected value", error.getErrorType(), is(StanzaErrorType.CANCEL));
	}

	@Test
	public void testGetCondition() {
		assertThat("Error condition has not the expected value", error.getCondition(), is(StanzaErrorCondition.UNDEFINED));
	}

	@Test
	public void testMakeErrorElement() {
		ErrorElement element = this.error.makeErrorElement();
		assertThat("Reference is expected to be of type StanzaErrorElement", element.getClass().getSimpleName(), equalTo("StanzaErrorElement"));
	}

}