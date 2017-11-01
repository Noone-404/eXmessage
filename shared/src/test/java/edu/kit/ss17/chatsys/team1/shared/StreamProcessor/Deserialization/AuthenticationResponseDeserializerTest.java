package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationResponse;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class AuthenticationResponseDeserializerTest {

	private AuthenticationResponseDeserializer authenticationResponseDeserializer;

	@Before
	public void setUp() throws Exception {
		authenticationResponseDeserializer = new AuthenticationResponseDeserializer();
	}

	@After
	public void tearDown() throws Exception {
		authenticationResponseDeserializer = null;
	}

	@Test
	public void deserializeXML() throws Exception {
		Document               successDocument = DocumentHelper.parseText("<success/>");
		Document               failureDocument = DocumentHelper.parseText("<failure/>");
		AuthenticationResponse success         = (AuthenticationResponse) authenticationResponseDeserializer.deserializeXML(successDocument);
		AuthenticationResponse failure         = (AuthenticationResponse) authenticationResponseDeserializer.deserializeXML(failureDocument);
		assertTrue("Wrong success value.", success.getAuthenticationSuccess());
		assertTrue("Wrong failure value.", !failure.getAuthenticationSuccess());
	}

}
