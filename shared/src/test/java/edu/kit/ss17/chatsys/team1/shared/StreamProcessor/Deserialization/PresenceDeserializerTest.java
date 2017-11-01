package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValue;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class PresenceDeserializerTest {

	private PresenceDeserializer presenceDeserializer;

	@Before
	public void setUp() throws Exception {
		presenceDeserializer = new PresenceDeserializer();
	}

	@Test
	public void deserializeXML() throws Exception {
		Document document = DocumentHelper.parseText("<presence " + "from=\"bob@xmpp.example.org/mobile\" " + "id=\"ko2ba41c\" "
		                                             + "to=\"alice@xmpp.example.org\" " + "client-send-date=\"456.456456456\" " + "server-receive-date=\"\" "
		                                             + "client-receive-date=\"\" " + "type=\"chat\">" + "<show>away</show>" + "<status>Testing eXmessage</status>" +
		                                             "</presence>");
		DataElementInterface dataElementInterface = presenceDeserializer.deserializeXML(document);
		assertTrue(dataElementInterface instanceof PresenceInterface);
		PresenceInterface presence = (PresenceInterface) dataElementInterface;
		assertTrue(presence.getID().equals("ko2ba41c") && presence.getSender().equals("bob@xmpp.example.org/mobile")
		           && presence.getReceiver().equals("alice@xmpp.example.org") && presence.getClientSendDate().equals(Instant.ofEpochSecond(456, 456456456))
		           && presence.getServerReceiveDate() == null && presence.getClientReceiveDate() == null
		           && presence.getPresence().equals(PresenceValue.AWAY) && presence.getStatus().equals("Testing eXmessage"));
	}

}
