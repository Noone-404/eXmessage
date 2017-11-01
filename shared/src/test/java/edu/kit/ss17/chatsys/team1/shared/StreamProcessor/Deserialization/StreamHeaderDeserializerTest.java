package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StreamHeaderInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.OpeningTagElement;
import org.dom4j.DocumentHelper;
import org.dom4j.Namespace;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class StreamHeaderDeserializerTest {

	private static final String FROM_VALUE = "test@example.com", ID_VALUE = "asdfasdf", TO_VALUE = "example.com", NAMESPACE_VALUE = "jabber:client";
	DeserializerInterface streamHeaderDeserializer;

	@Before
	public void setUp() throws Exception {
		streamHeaderDeserializer = new StreamHeaderDeserializer();
	}

	@Test
	public void deserializeXML() throws Exception {
		OpeningTagElement openingTagElement = new OpeningTagElement("stream");
		openingTagElement.addAttribute("from", FROM_VALUE);
		openingTagElement.addAttribute("id", ID_VALUE);
		openingTagElement.addAttribute("to", TO_VALUE);
		openingTagElement.setNamespace(new Namespace(null, NAMESPACE_VALUE));
		DataElementInterface dataElement = streamHeaderDeserializer.deserializeXML(DocumentHelper.createDocument(openingTagElement));
		assertTrue("Wrong data type.", dataElement instanceof StreamHeaderInterface);
		StreamHeaderInterface streamHeader = (StreamHeaderInterface) dataElement;
		assertTrue("Wrong sender.", streamHeader.getFrom().equals(FROM_VALUE));
		assertTrue("Wrong streamID.", streamHeader.getStreamID().equals(ID_VALUE));
		assertTrue("Wrong receiver.", streamHeader.getTo().equals(TO_VALUE));
		assertTrue("Wrong namespace.", streamHeader.getNamespace().equals(NAMESPACE_VALUE));
	}

}
