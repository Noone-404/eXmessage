package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.IQ;
import edu.kit.ss17.chatsys.team1.shared.StreamData.IQInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Iterator;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class IQDeserializerTest {

	IQDeserializer iQDeserializer;

	@Before
	public void setUp() {
		iQDeserializer = new IQDeserializer();
	}

	@Test
	public void deserializeXML() throws Exception {
		Document document = DocumentHelper.parseText("<iq " + "from=\"bob@xmpp.example.org/mobile\" " + "id=\"ko2ba41c\" "
		                                             + "to=\"xmpp.example.org\" " + "client-send-date=\"789.789789789\" " + "server-receive-date=\"\" "
		                                             + "client-receive-date=\"\" " + "type=\"get\">" + "<query namespace=\"jabber:iq:roster\">"
		                                             + "<content><special-content>asdfasdf</special-content></content>" + "</query></iq>"); // we don't actually have deserializers
		// for the content,
		// but test that no Exception occurs.
		DataElementInterface dataElementInterface = iQDeserializer.deserializeXML(document);
		assertTrue(dataElementInterface instanceof IQInterface);
		IQInterface iq = (IQInterface) dataElementInterface;
		assertTrue(iq.getID().equals("ko2ba41c") && iq.getSender().equals("bob@xmpp.example.org/mobile")
		           && iq.getReceiver().equals("xmpp.example.org") && iq.getClientSendDate().equals(Instant.ofEpochSecond(789, 789789789)));
		assertTrue(iq.getRequest().equals("query") && iq.getParameterNames().size() == 1 && iq.getParameterNames().iterator().next().equals("namespace")
		           && iq.getParameterValues().size() == 1 && iq.getParameterValues().iterator().next().equals("jabber:iq:roster"));
	}

	@Test
	public void deserializeDefaultContent() throws Exception {
		Document document = DocumentHelper.parseText("<iq " + "from =\"test.com\" " + "id=\"abcdef\" " + "to=\"user@test.com\" "
		                                            + "client-send-date=\"123.123123123\" " + "server-receive-date=\"234.234234234\" "
		                                            + "client-receive-date=\"\" " + "type=\"result\">" + "<jid-search jid-fragment=\"bob\">" +
		                                            "<content>\nbob@xmpp.example.com\nbob2@xmpp.example.com</content></jid-search></iq>");
		DataElementInterface dataElementInterface = iQDeserializer.deserializeXML(document);
		Assert.assertThat(dataElementInterface instanceof IQ, is(true));
		IQInterface iq = (IQInterface) dataElementInterface;
		Assert.assertThat(iq.getDefaultContent().size(), is(2));
		Iterator iterator = iq.getDefaultContent().iterator();
		Assert.assertThat(iterator.next(), is("bob@xmpp.example.com"));
		Assert.assertThat(iterator.next(), is("bob2@xmpp.example.com"));
	}
}
