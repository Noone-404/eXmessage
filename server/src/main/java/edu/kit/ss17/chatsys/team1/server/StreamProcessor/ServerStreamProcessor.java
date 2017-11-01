package edu.kit.ss17.chatsys.team1.server.StreamProcessor;

import edu.kit.ss17.chatsys.team1.server.StreamProcessor.Serialization.ServerMessageSerializer;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeature;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureSet;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StanzaErrorElement;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StreamErrorElement;
import edu.kit.ss17.chatsys.team1.shared.StreamData.*;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.AbstractStreamProcessor;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization.*;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.*;

/**
 * Server side StreamProcessor. See {@link AbstractStreamProcessor}.
 */
public class ServerStreamProcessor extends AbstractStreamProcessor {

	/**
	 * Constructor.
	 * Initializes all the deserializers.
	 */
	public ServerStreamProcessor() {
		deserializationManager = new DeserializationManager(this);
		deserializationManager.addDeserializerInterface(new MessageDeserializer());
		deserializationManager.addDeserializerInterface(new IQDeserializer());
		deserializationManager.addDeserializerInterface(new PresenceDeserializer());
		deserializationManager.addDeserializerInterface(new StreamFooterDeserializer());
		// note: it is important that the FooterDeserializer gets called before the HeaderDeserializer in the Chain
		deserializationManager.addDeserializerInterface(new StreamHeaderDeserializer());
		deserializationManager.addDeserializerInterface(new AuthenticationRequestDeserializer());
		deserializationManager.addDeserializerInterface(new AuthenticationResponseDeserializer());
		deserializationManager.addDeserializerInterface(new NegotiationFeatureSetDeserializer());
		deserializationManager.addDeserializerInterface(new StreamErrorDeserializer());
		deserializationManager.addDeserializerInterface(new StanzaErrorDeserializer());
	}

	/**
	 * This method sets up the StreamProcessor. To be called once at the program start.
	 */
	static void setUp() {
		setSerializers();
	}

	private static void setSerializers() {
		Message.setMessageSerializer(ServerMessageSerializer.getInstance());
		IQ.setIQSerializer(IQSerializer.getInstance());
		Presence.setPresenceSerializer(PresenceSerializer.getInstance());
		StreamHeader.setSerializer(StreamHeaderSerializer.getInstance());
		StreamFooter.setStreamFooterSerializer(StreamFooterSerializer.getInstance());
		AuthenticationRequest.setAuthenticationRequestSerializer(AuthenticationRequestSerializer.getInstance());
		AuthenticationResponse.setAuthenticationResponseSerializer(AuthenticationResponseSerializer.getInstance());
		NegotiationFeature.setNegotiationFeatureSerializer(NegotiationFeatureSerializer.getInstance());
		NegotiationFeatureSet.setNegotiationFeatureSetSerializer(NegotiationFeatureSetSerializer.getInstance());
		StreamErrorElement.setSerializerInterface(StreamErrorSerializer.getInstance());
		StanzaErrorElement.setSerializerInterface(StanzaErrorSerializer.getInstance());
	}
}
