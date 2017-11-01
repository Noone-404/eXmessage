package edu.kit.ss17.chatsys.team1.server.Session;

import edu.kit.ss17.chatsys.team1.server.Controller.ConnectionStackInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.*;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors.StreamProtocolError;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.*;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.Account;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainLowerDataProcessor;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 */
public class SessionTest {

	private ConnectionStackInterface                      connectionStack;
	private StorageInterface                              storageInterface;
	private SessionInterface                              session;
	private ChainLowerDataProcessor<DataElementInterface> chainLowerDataProcessor;

	@Captor
	private ArgumentCaptor<ProtocolErrorInterface> firstErrorCaptor = ArgumentCaptor.forClass(ProtocolErrorInterface.class);

	@Captor
	private ArgumentCaptor<ProtocolErrorInterface> secondErrorCaptor = ArgumentCaptor.forClass(ProtocolErrorInterface.class);
	@Captor
	private ArgumentCaptor<DataElementInterface>   captor            = ArgumentCaptor.forClass(DataElementInterface.class);

	@Before
	public void setUp() throws Exception {
		this.connectionStack = mock(ConnectionStackInterface.class);
		this.storageInterface = mock(StorageInterface.class);
		SessionFactory.getInstance().setStorage(this.storageInterface);
		this.session = SessionFactory.make();
		this.session.setConnectionStack(this.connectionStack);
		this.chainLowerDataProcessor = mock(ChainLowerDataProcessor.class);
		this.session.setLower(this.chainLowerDataProcessor);
	}

	@After
	public void tearDown() throws Exception {
		this.connectionStack = null;
		this.session = null;
		this.storageInterface = null;
		this.chainLowerDataProcessor = null;
	}

	@Test
	public void initTest() {
		assertFalse(this.session.isNegotiationFinished());
		assertFalse(this.session.isDisconnecting());
	}

	@Test
	public void initSession() throws Exception {
		JID                   jid          = new JID("test@example.com");
		StreamHeaderInterface streamHeader = new StreamHeader(jid);
		this.session.pushDataUp(streamHeader);
		verify(this.chainLowerDataProcessor, times(2)).pushDataDown(this.captor.capture());
		List<DataElementInterface>     arguments      = this.captor.getAllValues();
		Iterator<DataElementInterface> iterator       = arguments.iterator();
		DataElementInterface           firstArgument  = iterator.next();
		DataElementInterface           secondArgument = iterator.next();
		if (firstArgument instanceof StreamHeaderInterface) {
			StreamHeaderInterface header = (StreamHeaderInterface) firstArgument;
			assertEquals("jabber:server", header.getNamespace());
			assertEquals(jid.getFullJID(), header.getTo());
			assertEquals(jid.getDomainPart(), header.getFrom());
		} else {
			fail("First answer is not a Stream header");
		}
		if (secondArgument instanceof NegotiationFeatureSetInterface) {
			NegotiationFeatureSetInterface          featureSet = (NegotiationFeatureSetInterface) secondArgument;
			Collection<NegotiationFeatureInterface> features   = featureSet.getFeatures();
			assertTrue(features.size() == 1);
			Collection<NegotiationFeatureOptionInterface> options    = new ArrayList<>();
			Collection<String>                            mechanisms = new ArrayList<>();
			mechanisms.add("simple-Password-Authentication");
			NegotiationFeatureOptionInterface mechanismOption = new NegotiationFeatureOption("mechanism", mechanisms);
			options.add(mechanismOption);
			NegotiationFeatureInterface negotiationFeature = new NegotiationFeature("simple-Authentication", options, true);
			assertEquals(negotiationFeature, features.toArray()[0]);
		} else {
			fail("Second answer is not a Feature set");
		}
	}

	@Test
	public void terminateSession() throws Exception {
		JID                   jid          = new JID("test@example.com");
		StreamHeaderInterface streamHeader = new StreamHeader(jid);
		this.session.pushDataUp(streamHeader);
		this.session.terminateSession();
		verify(this.chainLowerDataProcessor, times(3)).pushDataDown(this.captor.capture());
		DataElementInterface lastArgument = this.captor.getValue();
		assertTrue(lastArgument instanceof StreamFooterInterface);
		assertTrue(this.session.isDisconnecting());
	}

	@Test
	public void setPresence() throws Exception {
		PresenceValueInterface presenceValue = PresenceValue.DND;
		this.session.setPresence(presenceValue);
		assertEquals(presenceValue, this.session.getPresence());
	}

	@Test
	public void setDisconnecting() throws Exception {
		this.session.setDisconnecting(true);
		assertTrue(this.session.isDisconnecting());
		this.session.setDisconnecting(false);
		assertFalse(this.session.isDisconnecting());
	}

	@Test
	public void negotiateAuthentication() throws InvalidJIDException {
		JID                   jid             = new JID("test@example.com");
		JID                   jidWithResource = new JID("test@example.com/resource");
		StreamHeaderInterface streamHeader    = new StreamHeader(jid);
		String                password        = "T3stp4ssw0rd";
		Account               account         = new Account(jid, password);
		when(this.storageInterface.getAccount(jid)).thenReturn(account);
		this.session.pushDataUp(streamHeader);
		AuthenticationRequest authenticationRequest = new AuthenticationRequest(jidWithResource, password);
		this.session.pushDataUp(authenticationRequest);
		streamHeader = new StreamHeader(jidWithResource);
		this.session.pushDataUp(streamHeader);
		verify(this.chainLowerDataProcessor, times(5)).pushDataDown(this.captor.capture());
		List<DataElementInterface> arguments      = this.captor.getAllValues();
		DataElementInterface       thirdArgument  = (DataElementInterface) arguments.toArray()[2];
		DataElementInterface       fourthArgument = (DataElementInterface) arguments.toArray()[3];
		DataElementInterface       fifthArgument  = (DataElementInterface) arguments.toArray()[4];
		if (thirdArgument instanceof AuthenticationResponse) {
			AuthenticationResponse authenticationResponse = (AuthenticationResponse) thirdArgument;
			assertTrue(authenticationResponse.getAuthenticationSuccess());
		} else {
			fail("Negotiation response has the wrong type");
		}
		if (fourthArgument instanceof StreamHeaderInterface) {
			StreamHeaderInterface responseHeader = (StreamHeaderInterface) fourthArgument;
			assertEquals("jabber:server", responseHeader.getNamespace());
			assertEquals(jidWithResource.getFullJID(), responseHeader.getTo());
			assertEquals(jidWithResource.getDomainPart(), responseHeader.getFrom());
		} else {
			fail("response Header after negotiation has wrong type");
		}
		if (fifthArgument instanceof NegotiationFeatureSet) {
			NegotiationFeatureSet                   featureSet = (NegotiationFeatureSet) fifthArgument;
			Collection<NegotiationFeatureInterface> features   = featureSet.getFeatures();
			assertTrue(features.isEmpty());
		} else {
			fail("No empty Feature Tag sent after negotiation finished");
		}
		assertTrue(this.session.isNegotiationFinished());
		verify(this.connectionStack).negotiationFinished(true);
		assertEquals(jidWithResource, this.session.getJid());
	}

	@Test
	public void optionalPlugin() throws InvalidJIDException {
		NegotiationFeature     feature             = mock(NegotiationFeature.class);
		DataElementInterface   negotiationRequest  = mock(DataElementInterface.class);
		DataElementInterface   negotiationResponse = mock(DataElementInterface.class);
		SessionPluginInterface plugin              = mock(SessionPluginInterface.class);
		when(plugin.isRequired()).thenReturn(false);
		when(plugin.getWeight()).thenReturn(8);
		when(plugin.hasNegotiationFailed()).thenReturn(false);
		when(plugin.isNegotiationFinished()).thenReturn(false);
		when(plugin.getNegotiationFeature()).thenReturn(feature);
		when(plugin.negotiate(negotiationRequest)).thenReturn(negotiationResponse);
		this.session.tryRegisterPlugin(plugin, true);
		JID                   jid          = new JID("test@example.com/resource");
		StreamHeaderInterface streamHeader = new StreamHeader(jid);
		this.session.pushDataUp(streamHeader);
		verify(this.chainLowerDataProcessor, times(2)).pushDataDown(this.captor.capture());
		DataElementInterface lastArgument = this.captor.getValue();
		if (lastArgument instanceof NegotiationFeatureSetInterface) {
			NegotiationFeatureSetInterface          featureSet = (NegotiationFeatureSetInterface) lastArgument;
			Collection<NegotiationFeatureInterface> features   = featureSet.getFeatures();
			assertTrue(features.contains(feature));
			assertTrue(features.size() == 2);
		} else {
			fail("No feature set was sent");
		}
		this.session.pushDataUp(negotiationRequest);
		verify(this.chainLowerDataProcessor).pushDataDown(negotiationResponse);
	}

	@Test
	public void requiredPlugin() throws InvalidJIDException {
		NegotiationFeature     feature             = mock(NegotiationFeature.class);
		DataElementInterface   negotiationRequest  = mock(DataElementInterface.class);
		DataElementInterface   negotiationResponse = mock(DataElementInterface.class);
		SessionPluginInterface plugin              = mock(SessionPluginInterface.class);
		when(plugin.isRequired()).thenReturn(true);
		when(plugin.getWeight()).thenReturn(5);
		when(plugin.hasNegotiationFailed()).thenReturn(false);
		when(plugin.isNegotiationFinished()).thenReturn(false);
		when(plugin.getNegotiationFeature()).thenReturn(feature);
		when(plugin.negotiate(negotiationRequest)).thenReturn(negotiationResponse);
		this.session.tryRegisterPlugin(plugin, true);
		JID                   jid          = new JID("test@example.com/resource");
		StreamHeaderInterface streamHeader = new StreamHeader(jid);
		this.session.pushDataUp(streamHeader);
		verify(this.chainLowerDataProcessor, times(2)).pushDataDown(this.captor.capture());
		DataElementInterface lastArgument = this.captor.getValue();
		if (lastArgument instanceof NegotiationFeatureSetInterface) {
			NegotiationFeatureSetInterface          featureSet = (NegotiationFeatureSetInterface) lastArgument;
			Collection<NegotiationFeatureInterface> features   = featureSet.getFeatures();
			assertTrue(features.contains(feature));
			assertTrue(features.size() == 1);
		} else {
			fail("No feature set was sent");
		}
		this.session.pushDataUp(negotiationRequest);
		verify(this.chainLowerDataProcessor).pushDataDown(negotiationResponse);
	}

	@Test
	public void invalidNegotiationData() throws Exception {
		DataElementInterface           invalidData         = mock(DataElementInterface.class);
		ProtocolErrorObserverInterface firstErrorObserver  = mock(ProtocolErrorObserverInterface.class);
		ProtocolErrorObserverInterface secondErrorObserver = mock(ProtocolErrorObserverInterface.class);
		this.session.registerErrorObserver(firstErrorObserver);
		this.session.registerErrorObserver(secondErrorObserver);
		JID                   jid          = new JID("test@example.com");
		StreamHeaderInterface streamHeader = new StreamHeader(jid);
		this.session.pushDataUp(streamHeader);
		this.session.pushDataUp(invalidData);
		this.session.unregisterErrorObserver(firstErrorObserver);
		this.session.pushDataUp(invalidData);
		verify(firstErrorObserver, times(1)).onProtocolError(this.firstErrorCaptor.capture());
		Collection<ProtocolErrorInterface> errors = this.firstErrorCaptor.getAllValues();
		verify(secondErrorObserver, times(2)).onProtocolError(this.secondErrorCaptor.capture());
		errors.addAll(this.secondErrorCaptor.getAllValues());
		for (final ProtocolErrorInterface currentError : errors) {
			assertTrue(currentError.isFatal());
			if (currentError instanceof StreamProtocolError) {
				StreamProtocolError streamProtocolError = (StreamProtocolError) currentError;
				assertEquals(StreamErrorCondition.UNDEFINED, streamProtocolError.getCondition());
			} else {
				fail("Error Element is not a StreamProtocolError");
			}
		}
	}

	@Test
	public void multipleStreamHeaders() throws Exception {
		ProtocolErrorObserverInterface errorObserver = mock(ProtocolErrorObserverInterface.class);
		this.session.registerErrorObserver(errorObserver);
		JID                   jid          = new JID("test@example.com");
		StreamHeaderInterface streamHeader = new StreamHeader(jid);
		this.session.pushDataUp(streamHeader);
		this.session.pushDataUp(streamHeader);
		verify(errorObserver, times(1)).onProtocolError(this.firstErrorCaptor.capture());
		ProtocolErrorInterface error = this.firstErrorCaptor.getValue();
		if (error instanceof StreamProtocolError) {
			StreamProtocolError streamProtocolError = (StreamProtocolError) error;
			assertEquals(StreamErrorCondition.POLICY, streamProtocolError.getCondition());
		} else {
			fail("Error Element is not a StreamProtocolError");
		}
	}

	@Test
	public void unauthenticatedJid() throws Exception {
		ProtocolErrorObserverInterface errorObserver = mock(ProtocolErrorObserverInterface.class);
		this.session.registerErrorObserver(errorObserver);
		JID                   jid             = new JID("test@example.com");
		JID                   jidWithResource = new JID("test@example.com/resource");
		JID                   wrongJid        = new JID("unauthanticated@test.com/ressource");
		StreamHeaderInterface streamHeader    = new StreamHeader(jid);
		String                password        = "T3stp4ssw0rd";
		Account               account         = new Account(jid, password);
		when(this.storageInterface.getAccount(jid)).thenReturn(account);
		this.session.pushDataUp(streamHeader);
		AuthenticationRequest authenticationRequest = new AuthenticationRequest(jidWithResource, password);
		this.session.pushDataUp(authenticationRequest);
		streamHeader = new StreamHeader(wrongJid);
		this.session.pushDataUp(streamHeader);
		verify(errorObserver, times(1)).onProtocolError(this.firstErrorCaptor.capture());
		ProtocolErrorInterface error = this.firstErrorCaptor.getValue();
		if (error instanceof StreamProtocolError) {
			StreamProtocolError streamProtocolError = (StreamProtocolError) error;
			assertEquals(StreamErrorCondition.INVALID_FROM, streamProtocolError.getCondition());
		} else {
			fail("Error Element is not a StreamProtocolError");
		}
	}

}
