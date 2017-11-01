package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.ErrorElement;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StreamErrorElement;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors.StreamProtocolError;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 */
public class StreamErrorSerializerTest {

	private StreamErrorSerializerInterface serializer;
	StreamErrorCondition condition = StreamErrorCondition.INTERNAL_ERROR;
	private final String message = "Out of Cheese Error";

	@Before
	public void setUp() {
		this.serializer = StreamErrorSerializer.getInstance();
	}

	@Test
	public void testSerialize() throws DocumentException {
		StreamProtocolError error   = new StreamProtocolError(condition, message);
		ErrorElement        element = error.makeErrorElement();

		Document correctXML = DocumentHelper
				.parseText("<stream-error>" + "<" + condition.getName() + "/>" + "<text>" + message + "</text>" + "</stream-error>");

		Document serializedXML = serializer.serialize(element);
		assertThat("Serialized XML document is not how it should be", serializedXML.getRootElement().asXML(), equalTo(correctXML.getRootElement().asXML()));
	}

}