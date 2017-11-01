package edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorType;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.StanzaErrorSerializerInterface;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class StanzaErrorElementTest {

	private StanzaErrorElement element;

	@Before
	public void setUp() {
		this.element = new StanzaErrorElement("", "","","test", StanzaErrorType.CANCEL, StanzaErrorCondition.UNDEFINED, "message", false);
	}

	@Test
	public void testSetSerializerInterface() {
		StanzaErrorSerializerInterface serializer = Mockito.mock(StanzaErrorSerializerInterface.class);
		StanzaErrorElement.setSerializerInterface(serializer);
		assertThat("Reference has not the expected value", StanzaErrorElement.getSerializerInterface(), is(serializer));
	}

	@Test
	public void testGetSerializerInterface() {
		StanzaErrorSerializerInterface serializer = Mockito.mock(StanzaErrorSerializerInterface.class);
		StanzaErrorElement.setSerializerInterface(serializer);
		StanzaErrorSerializerInterface reference = StanzaErrorElement.getSerializerInterface();
		assertThat("Reference has not the expected value", reference, is(serializer));
	}

	@Test
	public void testGetStanzaType() {
		assertThat("Stanza type has not the expected value", this.element.getStanzaID(), equalTo("test"));
	}

	@Test
	public void testGetErrorType() {
		assertThat("Error type has not the expected value", this.element.getErrorType(), is(StanzaErrorType.CANCEL));
	}

	@Test
	public void testGetCondition() {
		assertThat("Error condition has not the expected value", this.element.getCondition(), is(StanzaErrorCondition.UNDEFINED));
	}

	@Test
	public void testSerialize() {
		StanzaErrorSerializerInterface serializer = Mockito.mock(StanzaErrorSerializerInterface.class);
		StanzaErrorElement.setSerializerInterface(serializer);
		this.element.serialize();
		Mockito.verify(serializer).serialize(this.element);
	}

}