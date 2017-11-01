package edu.kit.ss17.chatsys.team1.shared.ProtocolTranslator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ProtocolTranslatorFactoryTest {

	ProtocolTranslatorFactory factory;

	@Before
	public void setUp() throws Exception {
		this.factory = ProtocolTranslatorFactory.getInstance();
	}

	@After
	public void tearDown() throws Exception {
		this.factory = null;
	}

	@Test
	public void make() throws Exception {
		ProtocolTranslatorInterface protocolTranslator = this.factory.makeInstance();
		assertTrue(protocolTranslator instanceof ProtocolTranslator);
		protocolTranslator = ProtocolTranslatorFactory.make();
		assertTrue(protocolTranslator instanceof ProtocolTranslator);
	}

}
