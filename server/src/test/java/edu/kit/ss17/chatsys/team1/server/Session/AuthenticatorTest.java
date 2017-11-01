package edu.kit.ss17.chatsys.team1.server.Session;

import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeature;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureOption;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureOptionInterface;
import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationRequest;
import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationResponse;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.Account;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class AuthenticatorTest {

	private Authenticator authenticator;

	@Before
	public void setUp() throws Exception {
		JID              jid     = new JID("test@example.com");
		Account          account = new Account(jid, "T3stp4ssw0rd");
		StorageInterface storage = mock(StorageInterface.class);
		when(storage.getAccount(jid)).thenReturn(account);
		this.authenticator = new Authenticator(storage);
	}

	@After
	public void tearDown() throws Exception {
		this.authenticator = null;
	}

	@Test
	public void isRequired() throws Exception {
		assertTrue(this.authenticator.isRequired());
	}

	@Test
	public void getNegotiationFeature() throws Exception {
		NegotiationFeature feature = this.authenticator.getNegotiationFeature();
		assertNotNull(feature);
		Collection<NegotiationFeatureOptionInterface> options    = new ArrayList<>();
		Collection<String>                            mechanisms = new ArrayList<>();
		mechanisms.add("simple-Password-Authentication");
		NegotiationFeatureOptionInterface mechanismOption = new NegotiationFeatureOption("mechanism", mechanisms);
		options.add(mechanismOption);
		NegotiationFeatureInterface negotiationFeature = new NegotiationFeature("simple-Authentication", options, true);
		assertEquals(negotiationFeature, feature);
	}

	@Test
	public void getWeight() throws Exception {
		assertEquals(10, this.authenticator.getWeight());
	}

	@Test
	public void initTest() {
		assertFalse(this.authenticator.isNegotiationFinished());
		assertFalse(this.authenticator.hasNegotiationFailed());
	}

	@Test
	public void successfulAuthentication() throws InvalidJIDException {
		JID                  jid                   = new JID("test@example.com");
		String               password              = "T3stp4ssw0rd";
		DataElementInterface authenticationRequest = new AuthenticationRequest(jid, password);
		DataElementInterface response              = this.authenticator.negotiate(authenticationRequest);
		if (response instanceof AuthenticationResponse) {
			AuthenticationResponse authenticationResponse = (AuthenticationResponse) response;
			assertTrue(authenticationResponse.getAuthenticationSuccess());
			assertTrue(this.authenticator.isNegotiationFinished());
			assertFalse(this.authenticator.hasNegotiationFailed());
			assertEquals(jid, this.authenticator.getJid());
		} else {
			fail("Negotiation response has wrong type");
		}
	}

	@Test
	public void wrongPassword() throws InvalidJIDException {
		JID                  jid                   = new JID("test@example.com");
		String               password              = "Wr0ngp4ssw0rd";
		DataElementInterface authenticationRequest = new AuthenticationRequest(jid, password);
		DataElementInterface response              = this.authenticator.negotiate(authenticationRequest);
		if (response instanceof AuthenticationResponse) {
			AuthenticationResponse authenticationResponse = (AuthenticationResponse) response;
			assertFalse(authenticationResponse.getAuthenticationSuccess());
			assertTrue(this.authenticator.isNegotiationFinished());
			assertTrue(this.authenticator.hasNegotiationFailed());
		} else {
			fail("Negotiation response has wrong type");
		}
	}

	@Test
	public void unknownJID() throws InvalidJIDException {
		JID                  jid                   = new JID("unknown@example.com");
		String               password              = "S0mep4ssw0rd";
		DataElementInterface authenticationRequest = new AuthenticationRequest(jid, password);
		DataElementInterface response              = this.authenticator.negotiate(authenticationRequest);
		if (response instanceof AuthenticationResponse) {
			AuthenticationResponse authenticationResponse = (AuthenticationResponse) response;
			assertFalse(authenticationResponse.getAuthenticationSuccess());
			assertTrue(this.authenticator.isNegotiationFinished());
			assertTrue(this.authenticator.hasNegotiationFailed());
		} else {
			fail("Negotiation response has wrong type");
		}
	}

	@Test(expected = IllegalStateException.class)
	public void getJid() {
		this.authenticator.getJid();
	}

}
