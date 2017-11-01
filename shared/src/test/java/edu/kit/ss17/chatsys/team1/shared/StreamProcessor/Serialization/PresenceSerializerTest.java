package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.Presence;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.PresenceValue;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Constants;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class PresenceSerializerTest {

	private PresenceSerializerInterface presenceSerializerInterface;

	@Before
	public void setUp() throws Exception {
		this.presenceSerializerInterface = PresenceSerializer.getInstance();
	}

	@Test
	public void serialize() throws Exception {
		PresenceInterface presence = new Presence();
		presence.setID("ko2ba41c");
		presence.setSender("bob@xmpp.example.org/mobile");
		presence.setReceiver("alice@xmpp.example.org");
		presence.setClientSendDate(Instant.ofEpochSecond(456, 456456456));
		presence.setType(Constants.AVAILABLE);
		presence.setPresence(PresenceValue.DND);
		presence.setStatus("Testing eXmessage.");

		Document correctXML = DocumentHelper.parseText('<' + Constants.PRESENCE + ' ' + "from=\"bob@xmpp.example.org/mobile\" " + "id=\"ko2ba41c\" "
		                                               + "to=\"alice@xmpp.example.org\" " + "client-send-date=\"456.456456456\" " + "server-receive-date=\"\" "
		                                               + "client-receive-date=\"\" " + "type=\"available\">" + "<show>dnd</show>" + "<status>Testing eXmessage.</status>" +
		                                               "</presence>");

		Document serializeXML = presenceSerializerInterface.serialize(presence);
		assertTrue(correctXML.getRootElement().asXML().equals(serializeXML.getRootElement().asXML()));
	}

}
