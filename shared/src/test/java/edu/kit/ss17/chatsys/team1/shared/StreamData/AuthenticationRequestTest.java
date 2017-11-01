package edu.kit.ss17.chatsys.team1.shared.StreamData;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.AuthenticationRequestSerializer;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.AuthenticationRequestSerializerInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

/**
 *
 */
public class AuthenticationRequestTest {

	@Mock
	private AuthenticationRequestSerializerInterface authenticationRequestSerializer;

	@Test
	public void getJID() throws Exception {
		JID                   jid                   = new JID("test@example.com");
		AuthenticationRequest authenticationRequest = new AuthenticationRequest(jid, "test123");
		assertTrue(jid.equals(authenticationRequest.getJID()));
	}

	@Test
	public void getPassword() throws Exception {
		JID                   jid                   = new JID("test@example.com");
		AuthenticationRequest authenticationRequest = new AuthenticationRequest(jid, "test123");
		assertTrue(authenticationRequest.getPassword() == "test123");
	}

	@Test
	public void equals() throws Exception {
		JID                   jid                   = new JID("test@example.com");
		String                password              = "TestPass";
		AuthenticationRequest authenticationRequest = new AuthenticationRequest(jid, password);
		AuthenticationRequest identicalRequest      = new AuthenticationRequest(jid, "TestPass");
		AuthenticationRequest differentPassRequest  = new AuthenticationRequest(jid, "DifferentPass");
		AuthenticationRequest differentJidRequest   = new AuthenticationRequest(new JID("different@domain.tld"), password);
		assertTrue(authenticationRequest.equals(authenticationRequest));
		assertTrue(authenticationRequest.equals(identicalRequest));
		assertFalse(authenticationRequest.equals(differentPassRequest));
		assertFalse(authenticationRequest.equals(differentJidRequest));
		assertFalse(authenticationRequest.equals(jid));
	}

	@Test
	public void serialize() throws Exception {
		MockitoAnnotations.initMocks(this);
		AuthenticationRequest.setAuthenticationRequestSerializer(authenticationRequestSerializer);
		Assert.assertThat(AuthenticationRequest.getAuthenticationRequestSerializer(), is(authenticationRequestSerializer));
		AuthenticationRequest authenticationRequest = new AuthenticationRequest(new JID("test@example.com"), "password");
		authenticationRequest.serialize();
		Mockito.verify(authenticationRequestSerializer, times(1)).serialize(authenticationRequest);
	}

	@Test
	public void testHashCode() throws Exception {
		AuthenticationRequest authenticationRequest = new AuthenticationRequest(new JID("a@a.a"), "b");
		Assert.assertThat(authenticationRequest.toString(), is("AuthenticationRequest{jid=a@a.a, password='b'}"));
		Assert.assertThat(authenticationRequest.hashCode(), is(-1455896306));
	}
}
