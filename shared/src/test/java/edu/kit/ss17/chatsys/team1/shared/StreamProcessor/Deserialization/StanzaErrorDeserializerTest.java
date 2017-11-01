package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorType;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StanzaErrorElement;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StreamErrorElement;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class StanzaErrorDeserializerTest {

	private StanzaErrorDeserializer stanzaErrorDeserializer;
	private final String sender = "bob@xmpp.example.org/mobile", receiver = "alice@xmpp.example.org", stanzaType = "message",
			stanzaID = "abc123def", message = "Divide By Cucumber Error. Please Reinstall Universe And Reboot";
	private final StanzaErrorType      errorType      = StanzaErrorType.CANCEL;
	private final StanzaErrorCondition errorCondition = StanzaErrorCondition.INTERNAL_ERROR;

	@Before
	public void setUp() throws Exception {
		stanzaErrorDeserializer = new StanzaErrorDeserializer();
	}

	@After
	public void tearDown() throws Exception {
		stanzaErrorDeserializer = null;
	}

	@Test
	public void deserializeXML() throws Exception {
		Document document = DocumentHelper
				.parseText("<" + stanzaType + " from=\"" + sender + "\" id = \"" + stanzaID + "\" to=\"" + receiver + "\" "
				           + "type=\"error\">" + "<error type=\"" + errorType.getName() + "\">" + "<" + errorCondition.getName()
				           + "/>" + "<text>" + message + "</text>" + "</error>" + "</" + stanzaType + ">");
		DataElementInterface dataElementInterface = stanzaErrorDeserializer.deserializeXML(document);
		assertTrue("Wrong data type.", dataElementInterface instanceof StanzaErrorElement);
		StanzaErrorElement stanzaErrorElement = (StanzaErrorElement) dataElementInterface;
		assertTrue("Wrong stanza type.", stanzaErrorElement.getStanzaType().equals(stanzaType));
		assertTrue("Wrong stanza id.", stanzaErrorElement.getStanzaID().equals(stanzaID));
		assertTrue("Wrong sender.", stanzaErrorElement.getSender().equals(sender));
		assertTrue("Wrong receiver.", stanzaErrorElement.getReceiver().equals(receiver));
		assertTrue("Wrong error type.", stanzaErrorElement.getErrorType().getName().equals(errorType.getName()));
		assertTrue("Wrong error condition.", stanzaErrorElement.getCondition().getName().equals(errorCondition.getName()));
		assertTrue("Wrong error message.", stanzaErrorElement.getMessage().equals(message));
	}

}