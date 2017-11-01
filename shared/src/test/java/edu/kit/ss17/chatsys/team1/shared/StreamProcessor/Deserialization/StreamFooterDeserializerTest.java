package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.StreamFooterInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ClosingTagElement;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class StreamFooterDeserializerTest {

	private static final String STREAM = "stream";
	DeserializerInterface streamFooterDeserializer;

	@Before
	public void setUp() {
		streamFooterDeserializer = new StreamFooterDeserializer();
	}

	@Test
	public void deserializeXML() throws Exception {
		assertTrue("Wrong datatype.", streamFooterDeserializer
				.deserializeXML(DocumentHelper.createDocument(new ClosingTagElement(STREAM))) instanceof StreamFooterInterface);
	}

}
