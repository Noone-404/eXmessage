package edu.kit.ss17.chatsys.team1.client.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.Message;
import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.DataElementSerializerInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.MessageSerializerInterface;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ClientMessageSerializerTest {

	MessageSerializerInterface clientMessageSerializer;

	@Before
	public void setUp() throws Exception {
		Message.setMessageSerializer(clientMessageSerializer = ClientMessageSerializer.getInstance());
		// Initialize the ClientMessageSerializer. In the actual program that happens in the StreamProcessor.setUp() method.
	}

	@Test
	public void serialize() throws Exception {
		MessageInterface message = new Message();
		message.setID("ko2ba41c");
		message.setSender("bob@xmpp.example.org/mobile");
		message.setReceiver("alice@xmpp.example.org");
		message.setClientSendDate(Instant.ofEpochSecond(456, 456456456));
		message.setPlaintextRepresentation("Das eXmessage-Programm funktioniert echt toll!");
		message.setType("chat");
		message.setExtendedContent(new ExtendedContent());

		Document correctXML = DocumentHelper.parseText("<message " + "from=\"bob@xmpp.example.org/mobile\" " + "id=\"ko2ba41c\" "
		                                               + "to=\"alice@xmpp.example.org\" " + "client-send-date=\"456.456456456\" " + "server-receive-date=\"\" "
		                                               + "client-receive-date=\"\" " + "type=\"chat\">" + "<body>Das eXmessage-Programm funktioniert echt toll!<extendedContent" +
		                                               ">Test</extendedContent></body>" + "</message>");

		Document serializeXML       = clientMessageSerializer.serialize(message);
		String   correctXMLString   = correctXML.asXML();
		String   serializeXMLString = serializeXML.asXML();
		assertTrue("Incorrect serialization", correctXML.getRootElement().asXML().equals(serializeXML.getRootElement().asXML()));
	}

	private class ExtendedContent implements DataElementInterface {

		@Override
		public Document serialize() {
			return new ExtendedContentSerializer().serialize(this);
		}
	}

	private class ExtendedContentSerializer implements DataElementSerializerInterface {

		@Override
		public Document serialize(DataElementInterface element) {
			try {
				return DocumentHelper.parseText("<extendedContent>Test</extendedContent>");
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
