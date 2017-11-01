package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorType;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.ErrorElement;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors.StanzaProtocolError;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 */
public class StanzaErrorSerializerTest {

	private StanzaErrorSerializerInterface serializer;
	private final String sender = "bob@xmpp.example.org/mobile", receiver = "alice@xmpp.example.org", stanzaType = "message",
			stanzaID = "abc123def", message = "Divide By Cucumber Error. Please Reinstall Universe And Reboot";
	private final StanzaErrorType errorType = StanzaErrorType.CANCEL;
	private final StanzaErrorCondition errorCondition = StanzaErrorCondition.INTERNAL_ERROR;

	@Before
	public void setUp() {
		this.serializer = StanzaErrorSerializer.getInstance();
	}

	@Test
	public void testSerialize() throws DocumentException {
		StanzaProtocolError error   = new StanzaProtocolError(sender, receiver, stanzaType, stanzaID,
		                                                      errorType, errorCondition, message, true);
		ErrorElement        element = error.makeErrorElement();

		Document correctXML = DocumentHelper
				.parseText("<" + stanzaType + " from=\"" + sender + "\" id=\"" + stanzaID + "\" to=\"" + receiver + "\" type=\"error\">"
				           + "<error type=\"" + errorType.getName() + "\">"
				           + "<" + errorCondition.getName() + "/>" + "<text>"
				           + message + "</text>" + "</error>"
				           + "</" + stanzaType + ">");

		Document serializedXML = serializer.serialize(element);
		assertThat("Serialized XML document is not how it should be", serializedXML.getRootElement().asXML(), equalTo(correctXML.getRootElement().asXML()));
	}

}