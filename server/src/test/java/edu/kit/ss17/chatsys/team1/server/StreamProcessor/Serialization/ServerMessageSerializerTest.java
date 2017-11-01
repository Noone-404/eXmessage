package edu.kit.ss17.chatsys.team1.server.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.Message;
import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.MessageSerializerInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ServerMessageSerializerTest {

	MessageSerializerInterface serverMessageSerializer;

	@Before
	public void setUp() throws Exception {
		Message.setMessageSerializer(serverMessageSerializer = ServerMessageSerializer.getInstance());
		// Initialize the ServerMessageSerializer. In the actual program that happens in the StreamProcessor.setUp() method.
	}

	@Test
	public void getExtendedContentXML() throws Exception {
	}

	@Test
	public void serialize() throws Exception {
		MessageInterface message = new Message();
		message.setID("ko2ba41c");
		message.setSender("bob@xmpp.example.org/mobile");
		message.setReceiver("alice@xmpp.example.org");
		message.setClientSendDate(Instant.ofEpochSecond(567, 567567567));
		message.setPlaintextRepresentation("Das eXmessage-Programm funktioniert echt toll!");
		message.setType("chat");
		message.setExtendedContentXML("<fancyContent>This is some fancy extendedContent</fancyContent>");

		Document correctXML = DocumentHelper.parseText("<message " + "from=\"bob@xmpp.example.org/mobile\" " + "id=\"ko2ba41c\" "
		                                               + "to=\"alice@xmpp.example.org\" " + "client-send-date=\"567.567567567\" " + "server-receive-date=\"\" "
		                                               + "client-receive-date=\"\" " + "type=\"chat\">" + "<body>Das eXmessage-Programm funktioniert echt toll!" +
		                                               "<fancyContent>This is some fancy extendedContent</fancyContent>" + "</body>" + "</message>");

		Document serializedXML    = serverMessageSerializer.serialize(message);
		String   correctString    = correctXML.getRootElement().asXML();
		String   serializedString = serializedXML.getRootElement().asXML();
		assertTrue(correctString.equals(serializedString));
	}

}
