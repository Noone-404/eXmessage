package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.AbstractStanza;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class AbstractStanzaSerializerTest {

	AbstractStanza           stanza;
	AbstractStanzaSerializer stanzaSerializer;

	@Before
	public void setUp() throws Exception {
		stanza = new ConcreteStanza();
		stanzaSerializer = new ConcreteStanzaSerializer();
	}

	@Test
	public void serializeMetadata() throws Exception {
		stanza.setID("ko2ba41c");
		stanza.setSender("bob@xmpp.example.org/mobile");
		stanza.setReceiver("alice@xmpp.example.org");
		stanza.setClientSendDate(Instant.ofEpochSecond(123456, 789012345));
		stanza.setType("type");

		Document correctXML = DocumentHelper.parseText("<stanza " + "from=\"bob@xmpp.example.org/mobile\" " + "id=\"ko2ba41c\" "
		                                               + "to=\"alice@xmpp.example.org\" " + "client-send-date=\"123456.789012345\" " + "server-receive-date=\"\" "
		                                               + "client-receive-date=\"\" " + "type=\"type\">" + "</stanza>");

		Document serializeXML = DocumentHelper.createDocument();
		Element  root         = serializeXML.addElement("stanza");
		root = stanzaSerializer.serializeMetadata(root, stanza);
		assertTrue(correctXML.getRootElement().asXML().equals(serializeXML.getRootElement().asXML()));
	}


	private static class ConcreteStanzaSerializer extends AbstractStanzaSerializer {

		@Override
		public Document serialize(DataElementInterface element) {
			return null;
		}
	}

	private static class ConcreteStanza extends AbstractStanza {

		@Override
		public Document serialize() {
			return null;
		}
	}
}
