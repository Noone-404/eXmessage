package edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.StreamErrorSerializerInterface;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class StreamErrorElementTest {

	private StreamErrorElement element;

	@Before
	public void setUp() {
		this.element = new StreamErrorElement(StreamErrorCondition.UNDEFINED, "test");
	}

	@Test
	public void testSetSerializerInterface() {
		StreamErrorSerializerInterface serializer = Mockito.mock(StreamErrorSerializerInterface.class);
		StreamErrorElement.setSerializerInterface(serializer);
		assertThat("Reference has not the expected value", StreamErrorElement.getSerializerInterface(), is(serializer));
	}

	@Test
	public void testGetSerializerInterface() {
		StreamErrorSerializerInterface serializer = Mockito.mock(StreamErrorSerializerInterface.class);
		StreamErrorElement.setSerializerInterface(serializer);
		StreamErrorSerializerInterface reference = StreamErrorElement.getSerializerInterface();
		assertThat("Reference has not the expected value", reference, is(serializer));
	}

	@Test
	public void testGetCondition() {
		assertThat("Error condition has not the expected value", this.element.getCondition(), is(StreamErrorCondition.UNDEFINED));
	}

	@Test
	public void testSerialize() {
		StreamErrorSerializerInterface serializer = Mockito.mock(StreamErrorSerializerInterface.class);
		StreamErrorElement.setSerializerInterface(serializer);
		this.element.serialize();
		Mockito.verify(serializer).serialize(this.element);
	}

}
