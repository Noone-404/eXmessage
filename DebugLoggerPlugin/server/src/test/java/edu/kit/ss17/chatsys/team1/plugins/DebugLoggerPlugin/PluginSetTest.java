package edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin;

import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.server.PluginSet;
import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared.DataElementSP;
import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared.DocumentSP;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class PluginSetTest {

	@Test
	public void getName() throws Exception {
		PluginSetInterface pluginSet = new PluginSet();
		assertThat("Name wrong", pluginSet.getName(), equalTo("Debug Logger - Server"));
	}

	@Test
	public void getDescription() throws Exception {
		PluginSetInterface pluginSet = new PluginSet();
		assertThat("Description wrong", pluginSet.getDescription(), equalTo("Helps debugging the flow of data structures through various components."));
	}

	@Test
	public void getVersion() throws Exception {
		PluginSetInterface pluginSet = new PluginSet();
		assertThat("Version wrong", pluginSet.getVersion(), equalTo("1.0"));
	}

	@Test
	public void getPlugins() throws Exception {
		PluginSetInterface pluginSet = new PluginSet();

		Collection<PluginInterface> plugins = pluginSet.getPlugins();

		// Expect 1 DataElementSP and 1 DocumentSP.
		Map<String, Boolean> seen = new HashMap<>();
		seen.put("DataElementSP", false);
		seen.put("DocumentSP", false);

		for (PluginInterface plugin : plugins) {
			assertThat("Unexpected plugin type", plugin, anyOf(instanceOf(DataElementSP.class), instanceOf(DocumentSP.class)));

			if (plugin instanceof DataElementSP) {
				assertThat("DataElementSP already seen", seen.get("DataElementSP"), equalTo(false));
				seen.put("DataElementSP", true);
			} else if (plugin instanceof DocumentSP) {
				assertThat("DocumentSP already seen", seen.get("DocumentSP"), equalTo(false));
				seen.put("DocumentSP", true);
			}
		}

		Collection<Boolean> seenValues = seen.values();
		assertThat("Either DataElementSP or DocumentSP plugin missing", seenValues, not(hasItem(false)));
	}

	@Test
	public void setGetEnabled() throws Exception {
		PluginSetInterface pluginSet = new PluginSet();
		assertThat("Pluginset should be disabled", pluginSet.getEnabledProperty().getValue(), equalTo(false));

		pluginSet.setEnabled(true);
		assertThat("Pluginset should be enabled", pluginSet.getEnabledProperty().getValue(), equalTo(true));
	}

}
