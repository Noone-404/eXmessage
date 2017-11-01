package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class MessageDeserializerTest {

	MessageDeserializer messageDeserializer;

	@Before
	public void setUp() {
		messageDeserializer = new MessageDeserializer();
	}

	@Test
	public void deserializeXML() throws Exception {
		Document document = DocumentHelper.parseText("<message " + "from=\"bob@xmpp.example.org/mobile\" " + "id=\"ko2ba41c\" "
		                                             + "to=\"alice@xmpp.example.org\" " + "client-send-date=\"345.345345345\" " + "server-receive-date=\"\" "
		                                             + "client-receive-date=\"\" " + "type=\"chat\">" +
		                                             "<body>Das eXmessage-Programm funktioniert echt toll!<test>Test</test></body></message>");
		messageDeserializer.addDeserializer(new ExtendedContentDeserializer());
		DataElementInterface dataElementInterface = messageDeserializer.deserializeXML(document);
		assertTrue(dataElementInterface instanceof MessageInterface);
		MessageInterface message = (MessageInterface) dataElementInterface;
		assertTrue("AbstractStanzaDeserializer error.", message.getID().equals("ko2ba41c") && message.getSender().equals("bob@xmpp.example.org/mobile")
		                                                && message.getReceiver().equals("alice@xmpp.example.org") &&
		                                                message.getClientSendDate().equals(Instant.ofEpochSecond(345, 345345345))
		                                                && message.getServerReceiveDate() == null && message.getClientReceiveDate() == null);
		assertTrue("Wrong plaintext.", message.getPlaintextRepresentation().equals("Das eXmessage-Programm funktioniert echt toll!"));
		assertTrue("Extended content error.", message.getExtendedContent() instanceof TestElement);
	}

	private class ExtendedContentDeserializer implements DeserializerInterface {

		@Nullable
		@Override
		public DataElementInterface deserializeXML(Branch branch) throws StanzaErrorException {
			DataElementInterface dataElement = null;
			if (branch.asXML().equals("<test>Test</test>")) {
				dataElement = new TestElement();
			}
			return dataElement;
		}
	}

	private class TestElement implements DataElementInterface {

		@Override
		public Document serialize() {
			return null;
		}
	}
}
