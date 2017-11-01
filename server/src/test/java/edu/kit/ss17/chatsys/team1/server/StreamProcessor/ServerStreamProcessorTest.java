package edu.kit.ss17.chatsys.team1.server.StreamProcessor;

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
public class ServerStreamProcessorTest {

	@Test
	public void setUp() throws Exception {
		ServerStreamProcessor.setUp();
		assertTrue("Message Serializer initialization failed.", Message.getMessageSerializer() != null);
		assertTrue("IQ Serializer initialization failed.", IQ.getIQSerializer() != null);
		assertTrue("Presence Serializer initialization failed.", Presence.getPresenceSerializer() != null);

		StreamProcessorInterface serverStreamProcessor  = new ServerStreamProcessor();
		Field                    deserializationManager = serverStreamProcessor.getClass().getSuperclass().getDeclaredField("deserializationManager");
		deserializationManager.setAccessible(true);
		String name = deserializationManager.getType().getSimpleName();
		assertTrue("Deserialization Manager initialization failed.", name.equals("DeserializationManagerInterface"));
	}

}
