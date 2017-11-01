package edu.kit.ss17.chatsys.team1.client.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.client.Model.RenderablePlaintextMessageStanza;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ContentDeserializerTest {

	private ContentDeserializer contentDeserializer;

	@Before
	public void setUp() {
		contentDeserializer = new ContentDeserializer();
	}

	@Test
	public void createMessageInterface() throws Exception {
		Document document = DocumentHelper.parseText("<message " + "id=\"ko2ba41c\" " + "from=\"bob@xmpp.example.org/mobile\" "
		                                             + "to=\"alice@xmpp.example.org\" " + "client-send-date=\"456.456456456\" " + "server-receive-date=\"\" "
		                                             + "client-receive-date=\"\" " + "type=\"chat\">" +
		                                             "<body>Das eXmessage-Programm funktioniert echt toll!</body></message>");

		DataElementInterface dataElementInterface = contentDeserializer.deserializeXML(document);
		assertTrue(dataElementInterface instanceof MessageInterface);
		MessageInterface message = (MessageInterface) dataElementInterface;
		assertTrue("AbstractStanzaDeserializer error.", message.getID().equals("ko2ba41c") && message.getSender().equals("bob@xmpp.example.org/mobile")
		                                                && message.getReceiver().equals("alice@xmpp.example.org") &&
		                                                message.getClientSendDate().equals(Instant.ofEpochSecond(456, 456456456))
		                                                && message.getServerReceiveDate() == null && message.getClientReceiveDate() == null);
		assertTrue("Wrong plaintext.", message.getPlaintextRepresentation().equals("Das eXmessage-Programm funktioniert echt toll!"));
		assertTrue("Wrong dataElement type.", message instanceof RenderablePlaintextMessageStanza);
	}

}
