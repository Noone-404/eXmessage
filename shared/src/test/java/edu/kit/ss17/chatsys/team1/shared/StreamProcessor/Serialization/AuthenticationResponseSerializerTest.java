package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationResponse;
import org.dom4j.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class AuthenticationResponseSerializerTest {

	AuthenticationResponseSerializerInterface serializer;

	@Before
	public void setUp() throws Exception {
		this.serializer = AuthenticationResponseSerializer.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		this.serializer = null;
	}

	@Test
	public void serialize() throws Exception {
		AuthenticationResponse successResponse = new AuthenticationResponse(true);
		AuthenticationResponse failureResponse = new AuthenticationResponse(false);
		Document               successDocument = this.serializer.serialize(successResponse);
		Document               failureDocument = this.serializer.serialize(failureResponse);
		assertEquals("<success/>", successDocument.getRootElement().asXML());
		assertEquals("<failure/>", failureDocument.getRootElement().asXML());
	}

}
