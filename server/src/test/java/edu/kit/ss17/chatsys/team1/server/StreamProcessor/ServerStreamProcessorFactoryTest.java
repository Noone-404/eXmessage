package edu.kit.ss17.chatsys.team1.server.StreamProcessor;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ServerStreamProcessorFactoryTest {

	@Test
	public void make() throws Exception {
		assertTrue("Wrong type.", ServerStreamProcessorFactory.make() instanceof ServerStreamProcessor);
	}

}
