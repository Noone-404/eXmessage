package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorType;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.ErrorElement;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StanzaErrorElement;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors.StanzaProtocolError;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.Message;
import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.StreamProcessorInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.omg.CORBA.DefinitionKindHelper;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 *
 */
public class DeserializationManagerTest {

	@Mock
	private DeserializerInterface deserializerInterfaceOne, deserializerInterfaceTwo, deserializerInterfaceThree, deserializerInterfaceFour;

	@Mock
	private StreamProcessorInterface streamProcessorInterface;

	private DeserializationManagerInterface deserializationManagerInterface;
	private Document                        document;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		deserializationManagerInterface = new DeserializationManager(streamProcessorInterface);
	}

	@Test
	public void deserializeXML() throws Exception {
		deserializationManagerInterface.addDeserializerInterface(deserializerInterfaceOne);
		deserializationManagerInterface.addDeserializerInterface(deserializerInterfaceTwo);
		deserializationManagerInterface.addDeserializerInterface(deserializerInterfaceThree);
		deserializationManagerInterface.addDeserializerInterface(deserializerInterfaceFour);
		document = DocumentHelper.createDocument();

		when(deserializerInterfaceOne.deserializeXML(document)).thenReturn(null);
		when(deserializerInterfaceTwo.deserializeXML(document)).thenReturn(null);
		when(deserializerInterfaceThree.deserializeXML(document)).thenReturn(new Message());
		when(deserializerInterfaceFour.deserializeXML(document)).thenReturn(null);

		assertTrue(deserializationManagerInterface.deserializeXML(document) instanceof MessageInterface);
		//deserializing this document does not really make sense, it's just for the test
		verify(deserializerInterfaceOne, times(1)).deserializeXML(document);
		verify(deserializerInterfaceTwo, times(1)).deserializeXML(document);
		verify(deserializerInterfaceThree, times(1)).deserializeXML(document);
		verify(deserializerInterfaceFour, times(0)).deserializeXML(document);
	}
}
