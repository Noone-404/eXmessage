package edu.kit.ss17.chatsys.team1.client.StreamProcessor;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ClientStreamProcessorFactoryTest {

	@Test
	public void make() throws Exception {
		assertTrue("Wrong type.", ClientStreamProcessorFactory.make() instanceof ClientStreamProcessor);
	}

}