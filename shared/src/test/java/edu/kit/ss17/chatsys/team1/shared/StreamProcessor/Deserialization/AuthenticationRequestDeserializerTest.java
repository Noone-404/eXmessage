package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationRequest;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class AuthenticationRequestDeserializerTest {

	private AuthenticationRequestDeserializer authenticationRequestDeserializer;

	@Before
	public void setUp() {
		this.authenticationRequestDeserializer = new AuthenticationRequestDeserializer();
	}

	@Test
	public void deserialize() throws Exception {
		Document document = DocumentHelper.parseText("<auth mechanism=\"simple-Password-Authentication\"><jid>test@example.com</jid><password>T3stP4ss</password></auth>");

		DataElementInterface dataElement = authenticationRequestDeserializer.deserializeXML(document);
		assertTrue(dataElement instanceof AuthenticationRequest);
		AuthenticationRequest authenticationRequest = (AuthenticationRequest) dataElement;
		assertTrue("Wrong JID.", "test@example.com".equals(authenticationRequest.getJID().getFullJID()));
		assertTrue("Wrong Password.", "T3stP4ss".equals(authenticationRequest.getPassword()));
		assertTrue("Wrong mechanism.", "simple-Password-Authentication".equals(authenticationRequest.getAuthMechanism()));
	}
}
