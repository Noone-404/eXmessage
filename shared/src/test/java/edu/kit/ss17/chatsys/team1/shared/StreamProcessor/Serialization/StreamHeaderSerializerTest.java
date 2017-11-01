package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.StreamHeader;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StreamHeaderInterface;
import org.dom4j.Document;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class StreamHeaderSerializerTest {

	private static final String STREAM = "stream", FROM = "from", STREAM_ID = "id", TO = "to", NAMESPACE = "xmlns",
			STREAM_ID_VALUE            = "asdfasdf", FROM_VALUE = "abc@example.com", TO_VALUE = "example.com", NAMESPACE_VALUE = "jabber:client";
	private StreamHeaderSerializerInterface streamHeaderSerializer;

	@Before
	public void setUp() throws Exception {
		streamHeaderSerializer = StreamHeaderSerializer.getInstance();
	}

	@Test
	public void serialize() throws Exception {
		StreamHeaderInterface streamHeader = new StreamHeader(STREAM_ID_VALUE, FROM_VALUE, TO_VALUE, NAMESPACE_VALUE);
		Document              document     = streamHeaderSerializer.serialize(streamHeader);
		Element               root         = document.getRootElement();
		assertTrue(root.getName().equals(STREAM));
		assertTrue(root.attribute(0).getName().equals(FROM) && root.attribute(0).getValue().equals(FROM_VALUE));
		assertTrue(root.attribute(1).getName().equals(STREAM_ID) && root.attribute(1).getValue().equals(STREAM_ID_VALUE));
		assertTrue(root.attribute(2).getName().equals(TO) && root.attribute(2).getValue().equals(TO_VALUE));
		assertTrue(root.getNamespaceURI().equals(NAMESPACE_VALUE));
	}

}
