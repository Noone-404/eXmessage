package edu.kit.ss17.chatsys.team1.client.Session;

import edu.kit.ss17.chatsys.team1.client.Controller.ConnectionStackInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.*;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors.StreamProtocolError;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.*;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.Account;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainLowerDataProcessorInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;
import javafx.beans.property.SimpleBooleanProperty;
import org.dom4j.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 */
public class SessionTest {

	private SessionInterface                                       session;
	private ChainLowerDataProcessorInterface<DataElementInterface> chainLowerDataProcessorInterface;
	private ConnectionStackInterface connectionStack;

	@Captor
	private ArgumentCaptor<DataElementInterface> captor = ArgumentCaptor.forClass(DataElementInterface.class);

	@Captor
	private ArgumentCaptor<ProtocolErrorInterface> firstErrorCaptor = ArgumentCaptor.forClass(ProtocolErrorInterface.class);

	@Captor
	private ArgumentCaptor<ProtocolErrorInterface> secondErrorCaptor = ArgumentCaptor.forClass(ProtocolErrorInterface.class);

	@Before
	public void setUp() throws Exception {
		this.session = SessionFactory.make();
		this.chainLowerDataProcessorInterface = mock(ChainLowerDataProcessorInterface.class);
		this.session.setLower(chainLowerDataProcessorInterface);
		this.connectionStack = mock(ConnectionStackInterface.class);
		this.session.setConnectionStack(this.connectionStack);
	}

	@After
	public void tearDown() throws Exception {
		this.session = null;
		this.chainLowerDataProcessorInterface = null;
		this.connectionStack = null;
	}


	@Test
	public void initTest() {
		assertFalse(this.session.isNegotiationFinished());
		assertFalse(this.session.isDisconnecting());
	}

	@Test
	public void setDisconnecting() {
		this.session.setDisconnecting(true);
		assertTrue(this.session.isDisconnecting());
	}

	@Test
	public void setPresence() {
		PresenceValueInterface presenceValue = PresenceValue.AWAY;
		this.session.setPresence(presenceValue);
		assertEquals(presenceValue, this.session.getPresence());
	}

	@Test
	public void initSession() throws InvalidJIDException {
		JID              jid      = new JID("test@example.com");
		String           password = "T3stp4ssp0rd";
		AccountInterface account  = new Account(jid, password);
		this.session.initSession(account);
		verify(this.chainLowerDataProcessorInterface).pushDataDown(this.captor.capture());
		DataElementInterface argument = this.captor.getValue();
		if (argument instanceof StreamHeaderInterface) {
			StreamHeaderInterface streamHeader = (StreamHeaderInterface) argument;
			assertEquals(jid.getLocalPart(), new JID(streamHeader.getFrom()).getLocalPart());
			assertEquals(jid.getDomainPart(), new JID(streamHeader.getFrom()).getDomainPart());
			assertNotNull(new JID(streamHeader.getFrom()).getResourcePart());
			assertEquals(jid.getDomainPart(), streamHeader.getTo());
			assertEquals("jabber:client", streamHeader.getNamespace());
		} else {
			fail("Data sent to lower has wrong type");
		}
	}

	@Test
	public void terminateSession() {
		this.session.terminateSession();
		verify(this.chainLowerDataProcessorInterface).pushDataDown(this.captor.capture());
		DataElementInterface argument = this.captor.getValue();
		assertTrue(argument instanceof StreamFooterInterface);
		assertTrue(this.session.isDisconnecting());
	}

	@Test
	public void successfulAuthentication() throws InvalidJIDException {
		JID              jid      = new JID("test@example.com");
		String           password = "T3stp4ssp0rd";
		AccountInterface account  = new Account(jid, password);
		this.session.initSession(account);
		verify(this.chainLowerDataProcessorInterface, times(1)).pushDataDown(this.captor.capture());
		DataElementInterface  sentData     = this.captor.getValue();
		StreamHeaderInterface streamHeader = null;
		if (sentData instanceof StreamHeaderInterface) {
			streamHeader = (StreamHeaderInterface) sentData;
		} else {
			fail("Data sent to lower has wrong type");
		}
		StreamHeaderInterface responseHeader = new StreamHeader(UUID.randomUUID().toString(), streamHeader.getTo(), streamHeader.getFrom(), "jabber:server");
		this.session.pushDataUp(responseHeader);
		Collection<NegotiationFeatureOptionInterface> options    = new ArrayList<>();
		Collection<String>                            mechanisms = new ArrayList<>();
		mechanisms.add("simple-Password-Authentication");
		NegotiationFeatureOptionInterface mechanismOption = new NegotiationFeatureOption("mechanism", mechanisms);
		options.add(mechanismOption);
		NegotiationFeatureInterface             feature  = new NegotiationFeature("simple-Authentication", options, true);
		Collection<NegotiationFeatureInterface> features = new LinkedList<>();
		features.add(feature);
		NegotiationFeatureSetInterface negotiationFeatureSet = new NegotiationFeatureSet(features);
		this.session.pushDataUp(negotiationFeatureSet);
		verify(this.chainLowerDataProcessorInterface, times(2)).pushDataDown(this.captor.capture());
		DataElementInterface argument = this.captor.getValue();
		if (argument instanceof AuthenticationRequest) {
			AuthenticationRequest authenticationRequest = (AuthenticationRequest) argument;
			assertEquals(jid, authenticationRequest.getJID());
			assertEquals(password, authenticationRequest.getPassword());
		} else {
			fail("Data sent to lower has wrong type");
		}
		AuthenticationResponse response = new AuthenticationResponse(true);
		this.session.pushDataUp(response);
		verify(this.chainLowerDataProcessorInterface, times(3)).pushDataDown(this.captor.capture());
		argument = this.captor.getValue();
		assertTrue(argument instanceof StreamHeaderInterface);
		this.session.pushDataUp(responseHeader);
		features = new LinkedList<>();
		negotiationFeatureSet = new NegotiationFeatureSet(features);
		this.session.pushDataUp(negotiationFeatureSet);
		assertTrue(this.session.isNegotiationFinished());
	}

	@Test
	public void pluginTest() {
		NegotiationFeature                      feature     = mock(NegotiationFeature.class);
		Collection<NegotiationFeatureInterface> featureList = new LinkedList<>();
		featureList.add(feature);
		NegotiationFeatureSetInterface features = new NegotiationFeatureSet(featureList);
		DataElementInterface negotiationResponse = new DataElementInterface() {
			@Override
			public Document serialize() {
				return null;
			}
		};
		SessionPluginInterface plugin = mock(SessionPluginInterface.class);
		when(plugin.getEnabledProperty()).thenReturn(new SimpleBooleanProperty(true));
		when(plugin.isNegotiationFinished()).thenReturn((false));
		when(plugin.hasNegotiationFailed()).thenReturn(false);
		when(plugin.startNegotiation(feature)).thenReturn(negotiationResponse);
		this.session.tryRegisterPlugin(plugin, true);
		this.session.pushDataUp(features);
		verify(this.chainLowerDataProcessorInterface).pushDataDown(this.captor.capture());
		DataElementInterface argument = this.captor.getValue();
		assertEquals(negotiationResponse, argument);
	}

	@Test
	public void unsupportedFeature() throws Exception {
		ProtocolErrorObserverInterface firstErrorObserver  = mock(ProtocolErrorObserverInterface.class);
		ProtocolErrorObserverInterface secondErrorObserver = mock(ProtocolErrorObserverInterface.class);
		this.session.registerErrorObserver(firstErrorObserver);
		this.session.registerErrorObserver(secondErrorObserver);
		NegotiationFeature feature = mock(NegotiationFeature.class);
		when(feature.isRequired()).thenReturn(true);
		Collection<NegotiationFeatureInterface> featureList = new LinkedList<>();
		featureList.add(feature);
		NegotiationFeatureSetInterface features = new NegotiationFeatureSet(featureList);
		this.session.pushDataUp(features);
		this.session.unregisterErrorObserver(firstErrorObserver);
		this.session.pushDataUp(features);
		verify(firstErrorObserver, times(1)).onProtocolError(this.firstErrorCaptor.capture());
		Collection<ProtocolErrorInterface> errors = this.firstErrorCaptor.getAllValues();
		verify(secondErrorObserver, times(2)).onProtocolError(this.secondErrorCaptor.capture());
		errors.addAll(this.secondErrorCaptor.getAllValues());
		for (final ProtocolErrorInterface currentError : errors) {
			assertTrue(currentError.isFatal());
			if (currentError instanceof StreamProtocolError) {
				StreamProtocolError streamProtocolError = (StreamProtocolError) currentError;
				assertEquals(StreamErrorCondition.UNSUPPORED_FEATURE, streamProtocolError.getCondition());
			} else {
				fail("Error is not a Stream Error");
			}
		}
	}

	@Test
	public void invalidResponseHeaders() throws InvalidJIDException {
		JID              jid      = new JID("test@example.com");
		String           password = "T3stp4ssp0rd";
		AccountInterface account  = new Account(jid, password);
		this.session.initSession(account);
		verify(this.chainLowerDataProcessorInterface, times(1)).pushDataDown(this.captor.capture());
		DataElementInterface  sentData     = this.captor.getValue();
		StreamHeaderInterface streamHeader = null;
		if (sentData instanceof StreamHeaderInterface) {
			streamHeader = (StreamHeaderInterface) sentData;
		} else {
			fail("Data sent to lower has wrong type");
		}
		ProtocolErrorObserverInterface errorObserver = mock(ProtocolErrorObserverInterface.class);
		this.session.registerErrorObserver(errorObserver);
		StreamHeaderInterface responseHeader = new StreamHeader(UUID.randomUUID().toString(), "wrongserver.test.com", streamHeader.getFrom(), "jabber:server");
		this.session.pushDataUp(responseHeader);
		verify(errorObserver, times(1)).onProtocolError(this.firstErrorCaptor.capture());
		ProtocolErrorInterface error = this.firstErrorCaptor.getValue();
		assertTrue(error.isFatal());
		if (error instanceof StreamProtocolError) {
			StreamProtocolError streamProtocolError = (StreamProtocolError) error;
			assertEquals(StreamErrorCondition.INVALID_FROM, streamProtocolError.getCondition());
		} else {
			fail("Error is not a Protocol Error");
		}
		responseHeader = new StreamHeader(UUID.randomUUID().toString(), streamHeader.getTo(), "someoneelse@example.com", "jabber:server");
		this.session.pushDataUp(responseHeader);
		verify(errorObserver, times(2)).onProtocolError(this.firstErrorCaptor.capture());
		error = this.firstErrorCaptor.getValue();
		assertTrue(error.isFatal());
		if (error instanceof StreamProtocolError) {
			StreamProtocolError streamProtocolError = (StreamProtocolError) error;
			assertEquals(StreamErrorCondition.HOST_UNKNOWN, streamProtocolError.getCondition());
		} else {
			fail("Error is not a Protocol Error");
		}
		responseHeader = new StreamHeader(UUID.randomUUID().toString(), streamHeader.getTo(), streamHeader.getFrom(), "jabber:client");
		this.session.pushDataUp(responseHeader);
		verify(errorObserver, times(3)).onProtocolError(this.firstErrorCaptor.capture());
		error = this.firstErrorCaptor.getValue();
		assertTrue(error.isFatal());
		if (error instanceof StreamProtocolError) {
			StreamProtocolError streamProtocolError = (StreamProtocolError) error;
			assertEquals(StreamErrorCondition.INVALID_NAMESPACE, streamProtocolError.getCondition());
		} else {
			fail("Error is not a Protocol Error");
		}
	}

	@Test
	public void failedAuthentication() throws Exception {
		JID                            jid           = new JID("test@example.com");
		String                         password      = "T3stp4ssp0rd";
		AccountInterface               account       = new Account(jid, password);
		ProtocolErrorObserverInterface errorObserver = mock(ProtocolErrorObserverInterface.class);
		this.session.registerErrorObserver(errorObserver);
		this.session.initSession(account);
		verify(this.chainLowerDataProcessorInterface, times(1)).pushDataDown(this.captor.capture());
		DataElementInterface  sentData     = this.captor.getValue();
		StreamHeaderInterface streamHeader = null;
		if (sentData instanceof StreamHeaderInterface) {
			streamHeader = (StreamHeaderInterface) sentData;
		} else {
			fail("Data sent to lower has wrong type");
		}
		StreamHeaderInterface responseHeader = new StreamHeader(UUID.randomUUID().toString(), streamHeader.getTo(), streamHeader.getFrom(), "jabber:server");
		this.session.pushDataUp(responseHeader);
		Collection<NegotiationFeatureOptionInterface> options    = new ArrayList<>();
		Collection<String>                            mechanisms = new ArrayList<>();
		mechanisms.add("simple-Password-Authentication");
		NegotiationFeatureOptionInterface mechanismOption = new NegotiationFeatureOption("mechanism", mechanisms);
		options.add(mechanismOption);
		NegotiationFeatureInterface             feature  = new NegotiationFeature("simple-Authentication", options, true);
		Collection<NegotiationFeatureInterface> features = new LinkedList<>();
		features.add(feature);
		NegotiationFeatureSetInterface negotiationFeatureSet = new NegotiationFeatureSet(features);
		this.session.pushDataUp(negotiationFeatureSet);
		verify(this.chainLowerDataProcessorInterface, times(2)).pushDataDown(this.captor.capture());
		DataElementInterface argument = this.captor.getValue();
		if (argument instanceof AuthenticationRequest) {
			AuthenticationRequest authenticationRequest = (AuthenticationRequest) argument;
			assertEquals(jid, authenticationRequest.getJID());
			assertEquals(password, authenticationRequest.getPassword());
		} else {
			fail("Data sent to lower has wrong type");
		}
		AuthenticationResponse response = new AuthenticationResponse(false);
		this.session.pushDataUp(response);
		verify(this.connectionStack).negotiationFinished(false);
	}

	@Test
	public void pluginAfterAuthentication() throws Exception {
		NegotiationFeatureInterface optionalFeature = mock(NegotiationFeatureInterface.class);
		when(optionalFeature.isRequired()).thenReturn(false);
		DataElementInterface   negotiationData     = mock(DataElementInterface.class);
		DataElementInterface   negotiationResponse = mock(DataElementInterface.class);
		SessionPluginInterface plugin              = mock(SessionPluginInterface.class);
		when(plugin.getWeight()).thenReturn(20);
		when(plugin.startNegotiation(optionalFeature)).thenReturn(negotiationData);
		this.session.tryRegisterPlugin(plugin, true);
		JID              jid      = new JID("test@example.com");
		String           password = "T3stp4ssp0rd";
		AccountInterface account  = new Account(jid, password);
		this.session.initSession(account);
		verify(this.chainLowerDataProcessorInterface, times(1)).pushDataDown(this.captor.capture());
		DataElementInterface  sentData     = this.captor.getValue();
		StreamHeaderInterface streamHeader = null;
		if (sentData instanceof StreamHeaderInterface) {
			streamHeader = (StreamHeaderInterface) sentData;
		} else {
			fail("Data sent to lower has wrong type");
		}
		StreamHeaderInterface responseHeader = new StreamHeader(UUID.randomUUID().toString(), streamHeader.getTo(), streamHeader.getFrom(), "jabber:server");
		this.session.pushDataUp(responseHeader);
		Collection<NegotiationFeatureOptionInterface> options    = new ArrayList<>();
		Collection<String>                            mechanisms = new ArrayList<>();
		mechanisms.add("simple-Password-Authentication");
		NegotiationFeatureOptionInterface mechanismOption = new NegotiationFeatureOption("mechanism", mechanisms);
		options.add(mechanismOption);
		NegotiationFeatureInterface             feature  = new NegotiationFeature("simple-Authentication", options, true);
		Collection<NegotiationFeatureInterface> features = new LinkedList<>();
		features.add(feature);
		NegotiationFeatureSetInterface negotiationFeatureSet = new NegotiationFeatureSet(features);
		this.session.pushDataUp(negotiationFeatureSet);
		AuthenticationResponse response = new AuthenticationResponse(true);
		this.session.pushDataUp(response);
		this.session.pushDataUp(responseHeader);
		features = new LinkedList<>();
		features.add(optionalFeature);
		negotiationFeatureSet = new NegotiationFeatureSet(features);
		this.session.pushDataUp(negotiationFeatureSet);
		verify(this.chainLowerDataProcessorInterface).pushDataDown(negotiationData);
		when(plugin.isNegotiationFinished()).thenReturn(true);
		this.session.pushDataUp(negotiationResponse);
		assertTrue(this.session.isNegotiationFinished());
	}

}
