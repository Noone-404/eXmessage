package edu.kit.ss17.chatsys.team1.client.StreamProcessor;

import edu.kit.ss17.chatsys.team1.client.StreamProcessor.Deserialization.ContentDeserializer;
import edu.kit.ss17.chatsys.team1.client.StreamProcessor.Serialization.ClientMessageSerializer;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeature;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureSet;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StanzaErrorElement;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StreamErrorElement;
import edu.kit.ss17.chatsys.team1.shared.StreamData.*;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.AbstractStreamProcessor;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization.*;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.*;

/**
 * Client side StreamProcessor. See {@link AbstractStreamProcessor}
 */
public class ClientStreamProcessor extends AbstractStreamProcessor {

	/**
	 * Constructor. Initialize all the deserializers.
	 */
	public ClientStreamProcessor() {
		deserializationManager = new DeserializationManager(this);
		deserializationManager.addDeserializerInterface(new ContentDeserializer());
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
	 * This method sets up the StreamProcessor class. This method should be used once at the program start.
	 */
	static void setUp() {
		setSerializers(); // sets up the serializers
	}

	private static void setSerializers() {
		Message.setMessageSerializer(ClientMessageSerializer.getInstance());
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
