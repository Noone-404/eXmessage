package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.AbstractStanza;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StanzaInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class AbstractStanzaDeserializerTest {

	AbstractStanzaDeserializer abstractStanzaDeserializer;
	AbstractStanza             abstractStanza;

	@Before
	public void setUp() {
		this.abstractStanzaDeserializer = new ConcreteStanzaDeserializer();
		this.abstractStanza = new ConcreteStanza();
	}

	@Test
	public void deserializeMetadata() throws Exception {
		Document document = DocumentHelper.parseText("<message " + "from=\"bob@xmpp.example.org/mobile\" " + "id=\"ko2ba41c\" "
		                                             + "to=\"alice@xmpp.example.org\" " + "client-send-date=\"678.678678678\" " + "server-receive-date=\"\" "
		                                             + "client-receive-date=\"\" " + "type=\"chat\">" + "<body>Das eXmessage-Programm funktioniert echt toll!</body>" +
		                                             "</message>");
		DataElementInterface dataElementInterface = abstractStanzaDeserializer.deserializeMetadata(document.getRootElement(), abstractStanza);
		assertTrue(dataElementInterface instanceof StanzaInterface);
		ConcreteStanza concreteStanza = (ConcreteStanza) dataElementInterface;
		assertTrue(concreteStanza.getID().equals("ko2ba41c") && concreteStanza.getSender().equals("bob@xmpp.example.org/mobile")
		           && concreteStanza.getReceiver().equals("alice@xmpp.example.org") && concreteStanza.getClientSendDate().equals(Instant.ofEpochSecond(678, 678678678))
		           && concreteStanza.getServerReceiveDate() == null && concreteStanza.getClientReceiveDate() == null);
	}

	private class ConcreteStanzaDeserializer extends AbstractStanzaDeserializer {

		@Override
		public DataElementInterface deserializeXML(Branch branch) throws StanzaErrorException {
			return null;
		}
	}

	private class ConcreteStanza extends AbstractStanza {

		@Override
		public Document serialize() {
			return null;
		}
	}
}
