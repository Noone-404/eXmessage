package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.IQ;
import edu.kit.ss17.chatsys.team1.shared.StreamData.IQInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class IQSerializerTest {

	IQSerializerInterface iqSerializer;

	@Before
	public void setUp() throws Exception {
		iqSerializer = IQSerializer.getInstance();
	}

	@Test
	public void serialize() throws Exception {
		IQInterface iq = new IQ();
		iq.setID("ko2ba41c");
		iq.setSender("bob@xmpp.example.org/mobile");
		iq.setReceiver("alice@xmpp.example.org");
		iq.setClientSendDate(Instant.ofEpochSecond(345, 345345345));
		iq.setType("get");
		iq.setRequest("query");
		iq.addParameterName("namespace");
		iq.addParameterValue("jabber:iq:roster");

		Document correctXML = DocumentHelper.parseText("<iq " + "from=\"bob@xmpp.example.org/mobile\" " + "id=\"ko2ba41c\" "
		                                               + "to=\"alice@xmpp.example.org\" " + "client-send-date=\"345.345345345\" " + "server-receive-date=\"\" "
		                                               + "client-receive-date=\"\" " + "type=\"get\">" + "<query namespace=\"jabber:iq:roster\"/>" + "</iq>");

		Document serializeXML = iqSerializer.serialize(iq);
		Assert.assertThat(correctXML.getRootElement().asXML(), is(serializeXML.getRootElement().asXML()));
	}

	@Test
	public void serializerDefaultContent() throws Exception {
		IQInterface iq = new IQ();
		iq.setID("abcdef");
		iq.setSender("test.com");
		iq.setReceiver("user@test.com");
		iq.setClientSendDate(Instant.ofEpochSecond(123, 123123123));
		iq.setServerReceiveDate(Instant.ofEpochSecond(234, 234234234));
		iq.setType("result");
		iq.setRequest("jid-search");
		iq.addParameterName("jid-fragment");
		iq.addParameterValue("bob");
		iq.addDefaultContent("bob@xmpp.example.com");
		iq.addDefaultContent("bob2@xmpp.example.com");

		Document correctXML = DocumentHelper.parseText("<iq " + "from =\"test.com\" " + "id=\"abcdef\" " + "to=\"user@test.com\" "
		                                              + "client-send-date=\"123.123123123\" " + "server-receive-date=\"234.234234234\" "
		                                              + "client-receive-date=\"\" " + "type=\"result\">" + "<jid-search jid-fragment=\"bob\">" +
		                                               "<content>\nbob@xmpp.example.com\nbob2@xmpp.example.com</content></jid-search></iq>");

		Document serializedXML = iqSerializer.serialize(iq);
		Assert.assertThat(correctXML.getRootElement().asXML(), is(serializedXML.getRootElement().asXML()));
	}
}
