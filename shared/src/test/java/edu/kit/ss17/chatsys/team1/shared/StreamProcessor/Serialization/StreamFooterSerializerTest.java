package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.StreamFooter;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StreamFooterInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ClosingTagElement;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class StreamFooterSerializerTest {

	private static final String STREAM = "stream";
	StreamFooterSerializerInterface serializer;

	@Before
	public void setUp() {
		serializer = StreamFooterSerializer.getInstance();
	}

	@Test
	public void serialize() throws Exception {
		StreamFooterInterface streamFooter = new StreamFooter();
		Document              document     = serializer.serialize(streamFooter);
		Document              correctXML   = DocumentHelper.createDocument(new ClosingTagElement(STREAM));
		assertTrue("Wrong serialization.", document.asXML().equals(correctXML.asXML()));
	}

}
