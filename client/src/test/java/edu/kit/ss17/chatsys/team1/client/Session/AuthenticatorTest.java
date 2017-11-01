package edu.kit.ss17.chatsys.team1.client.Session;

import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeature;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureOption;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureOptionInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationRequest;
import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationResponse;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.Account;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;


/**
 *
 */
public class AuthenticatorTest {

	Authenticator authenticator;
	JID           jid;
	String        password;

	@Before
	public void setUp() throws Exception {
		this.jid = new JID("test@example.com");
		this.password = "test123";
		Account account = new Account(this.jid, this.password);
		authenticator = new Authenticator(account);
	}

	@After
	public void tearDown() throws Exception {
		this.authenticator = null;
	}

	@Test
	public void initTest() {
		assertFalse(authenticator.hasNegotiationFailed());
		assertFalse(authenticator.isNegotiationFinished());
	}

	@Test
	public void startNegotiation() {
		Collection<NegotiationFeatureOptionInterface> options    = new ArrayList<>();
		Collection<String>                            mechanisms = new ArrayList<>();
		mechanisms.add("simple-Password-Authentication");
		NegotiationFeatureOptionInterface mechanismOption = new NegotiationFeatureOption("mechanism", mechanisms);
		options.add(mechanismOption);
		NegotiationFeatureInterface negotiationFeature = new NegotiationFeature("simple-Authentication", options, true);
		DataElementInterface        negotiationData    = authenticator.startNegotiation(negotiationFeature);
		assertTrue(negotiationData instanceof AuthenticationRequest);
		AuthenticationRequest authenticationRequest = (AuthenticationRequest) negotiationData;
		assertEquals(this.jid, authenticationRequest.getJID());
		assertEquals(this.password, authenticationRequest.getPassword());
	}

	@Test
	public void successfulResponse() {
		DataElementInterface negotiationResponse = new AuthenticationResponse(true);
		DataElementInterface reaction            = authenticator.negotiate(negotiationResponse);
		assertNull(reaction);
		assertTrue(authenticator.isNegotiationFinished());
		assertFalse(authenticator.hasNegotiationFailed());
	}

	@Test
	public void failedAuthentication() {
		DataElementInterface negotiationResponse = new AuthenticationResponse(false);
		DataElementInterface reaction            = authenticator.negotiate(negotiationResponse);
		assertNull(reaction);
		assertTrue(authenticator.isNegotiationFinished());
		assertTrue(authenticator.hasNegotiationFailed());
	}

	@Test
	public void wrongFeatureElement() {
		Collection<NegotiationFeatureOptionInterface> options  = new ArrayList<>();
		NegotiationFeatureInterface                   feature  = new NegotiationFeature("not-Authentication", options, true);
		DataElementInterface                          response = this.authenticator.startNegotiation(feature);
		assertNull(response);
	}

	@Test
	public void noMechanisms() {
		Collection<NegotiationFeatureOptionInterface> options  = new ArrayList<>();
		NegotiationFeatureInterface                   feature  = new NegotiationFeature("simple-Authentication", options, true);
		DataElementInterface                          response = this.authenticator.startNegotiation(feature);
		assertNull(response);
	}

	@Test
	public void noSupportedMechanism() {
		Collection<NegotiationFeatureOptionInterface> options    = new ArrayList<>();
		Collection<String>                            mechanisms = new ArrayList<>();
		mechanisms.add("unsupported-Mechanism");
		NegotiationFeatureOptionInterface mechanismOption = new NegotiationFeatureOption("mechanism", mechanisms);
		options.add(mechanismOption);
		NegotiationFeatureInterface feature  = new NegotiationFeature("simple-Authentication", options, true);
		DataElementInterface        response = this.authenticator.startNegotiation(feature);
		assertNull(response);
	}
}
