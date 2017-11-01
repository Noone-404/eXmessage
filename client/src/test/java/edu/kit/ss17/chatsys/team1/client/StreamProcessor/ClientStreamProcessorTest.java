package edu.kit.ss17.chatsys.team1.client.StreamProcessor;

import edu.kit.ss17.chatsys.team1.shared.StreamData.IQ;
import edu.kit.ss17.chatsys.team1.shared.StreamData.Message;
import edu.kit.ss17.chatsys.team1.shared.StreamData.Presence;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.StreamProcessorInterface;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ClientStreamProcessorTest {

	@Test
	public void setUp() throws Exception {
		ClientStreamProcessor.setUp();
		assertTrue("Message Serializer initialization failed.", Message.getMessageSerializer() != null);
		assertTrue("IQ Serializer initialization failed.", IQ.getIQSerializer() != null);
		assertTrue("Presence Serializer initialization failed.", Presence.getPresenceSerializer() != null);

		StreamProcessorInterface clientStreamProcessor  = new ClientStreamProcessor();
		Field                    deserializationManager = clientStreamProcessor.getClass().getSuperclass().getDeclaredField("deserializationManager");
		deserializationManager.setAccessible(true);
		String name = deserializationManager.getType().getSimpleName();
		assertTrue("Deserialization Manager initialization failed.", name.equals("DeserializationManagerInterface"));
	}

}