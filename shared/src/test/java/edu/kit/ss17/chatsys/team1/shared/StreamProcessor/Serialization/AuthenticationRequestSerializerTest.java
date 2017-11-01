package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationRequest;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import org.dom4j.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class AuthenticationRequestSerializerTest {

	private AuthenticationRequestSerializerInterface serializer;

	@Before
	public void setUp() throws Exception {
		this.serializer = AuthenticationRequestSerializer.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		this.serializer = null;
	}

	@Test
	public void serialize() throws Exception {
		JID                   jid               = new JID("test@example.com");
		AuthenticationRequest request           = new AuthenticationRequest(jid, "T3stP4ss");
		Document              resultingDocument = this.serializer.serialize(request);
		assertEquals("<auth mechanism=\"simple-Password-Authentication\"><jid>test@example.com</jid><password>T3stP4ss</password></auth>",
		             resultingDocument.getRootElement().asXML());
	}

}
