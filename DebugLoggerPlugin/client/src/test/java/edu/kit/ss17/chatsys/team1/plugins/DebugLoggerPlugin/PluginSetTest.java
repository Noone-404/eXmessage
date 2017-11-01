package edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin;

import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.client.ByteTestSP;
import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.client.GuiMenuPlugin;
import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.client.PluginSet;
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
		assertThat("Name wrong", pluginSet.getName(), equalTo("Debug Logger - Client"));
	}

	@Test
	public void getDescription() throws Exception {
		PluginSetInterface pluginSet = new PluginSet();
		assertThat("Description wrong", pluginSet.getDescription(), equalTo("Provides various debugging features."));
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

		// Expect 1 DataElementSP and 1 DocumentSP and 1 GuiMenuPlugin.
		Map<String, Boolean> seen = new HashMap<>();
		seen.put("DataElementSP", false);
		seen.put("DocumentSP", false);
		seen.put("GuiMenuPlugin", false);
		seen.put("ByteTestSP", false);

		for (PluginInterface plugin : plugins) {
			assertThat("Unexpected plugin type", plugin, anyOf(instanceOf(DataElementSP.class), instanceOf(DocumentSP.class), instanceOf(GuiMenuPlugin.class), instanceOf
					(ByteTestSP.class)));

			if (plugin instanceof DataElementSP) {
				assertThat("DataElementSP already seen", seen.get("DataElementSP"), equalTo(false));
				seen.put("DataElementSP", true);
			} else if (plugin instanceof DocumentSP) {
				assertThat("DocumentSP already seen", seen.get("DocumentSP"), equalTo(false));
				seen.put("DocumentSP", true);
			} else if (plugin instanceof GuiMenuPlugin) {
				assertThat("GuiMenuPlugin already seen", seen.get("GuiMenuPlugin"), equalTo(false));
				seen.put("GuiMenuPlugin", true);
			} else if (plugin instanceof ByteTestSP) {
				assertThat("ByteTestSP already seen", seen.get("ByteTestSP"), equalTo(false));
				seen.put("ByteTestSP", true);
			}
		}

		Collection<Boolean> seenValues = seen.values();
		assertThat("Either DataElementSP, DocumentSP or GuiMenuPlugin missing", seenValues, not(hasItem(false)));
	}

	@Test
	public void setGetEnabled() throws Exception {
		PluginSetInterface pluginSet = new PluginSet();
		assertThat("Pluginset should be disabled", pluginSet.getEnabledProperty().getValue(), equalTo(false));

		pluginSet.setEnabled(true);
		assertThat("Pluginset should be enabled", pluginSet.getEnabledProperty().getValue(), equalTo(true));
	}

}