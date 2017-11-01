package edu.kit.ss17.chatsys.team1.plugins.TCPConnectionPlugin;

import org.jetbrains.annotations.NonNls;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class PluginSetTest {

	@SuppressWarnings("DuplicateStringLiteralInspection")
	@NonNls
	private static final String DESC = "Provides basic functionality for sending and receiving messages using the TCP/IP protocol suite.";
	private PluginSet set;

	@Before
	public void setUp() {
		this.set = new PluginSet();
	}

	@Test
	public void testGetName() {
		assertThat("Name has to match", this.set.getName(), is("TCP"));
	}

	@Test
	public void testGetDescription() {
		assertThat("Description has to match", this.set.getDescription(), is(DESC));
	}

	@Test
	public void testGetVersion() {
		assertThat("Version has to match", this.set.getVersion(), is("1.0"));
	}

	@Test
	public void testGetPlugins() {
		assertThat("Certain plugin count expected", this.set.getPlugins().size(), is(2));
	}

	@Test
	public void testIsEnabled() {
		assertThat("Pluginset has to enabled per default", this.set.getEnabledProperty().getValue(), is(true));
	}

	@Test
	public void testClone() {
		PluginSet newSet = this.set.clone();
		assertThat("Cloned set must not be null", newSet, notNullValue());
	}
}
