package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StreamErrorElement;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class StreamErrorDeserializerTest {

	private StreamErrorDeserializer streamErrorDeserializer;
	StreamErrorCondition condition = StreamErrorCondition.INTERNAL_ERROR;
	private final String message = "Out of Cheese Error";

	@Before
	public void setUp() throws Exception {
		this.streamErrorDeserializer = new StreamErrorDeserializer();
	}

	@After
	public void tearDown() throws Exception {
		this.streamErrorDeserializer = null;
	}

	@Test
	public void deserializeXML() throws Exception {
		Document document = DocumentHelper.parseText("<stream-error>" + "<" + condition.getName()
		                                             + "/>" + "<text>" + message + "</text>" + "</stream-error>");
		DataElementInterface dataElementInterface = streamErrorDeserializer.deserializeXML(document);
		assertTrue("Wrong stanza type.", dataElementInterface instanceof StreamErrorElement);
		StreamErrorElement streamErrorElement = (StreamErrorElement) dataElementInterface;
		assertTrue("Wrong error condition.", streamErrorElement.getCondition().getName().equals(this.condition.getName()));
		assertTrue("Wrong error message.", streamErrorElement.getMessage().equals(this.message));
	}

}