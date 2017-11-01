package edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin;

import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.client.GuiMenuPlugin;
import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared.DataElementSP;
import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared.DocumentSP;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import javafx.scene.control.MenuItem;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 *
 */
public class GuiMenuPluginTest {

	GuiMenuPlugin plugin;
	PluginSetInterface pluginSet = mock(PluginSetInterface.class);

	@Before
	public void setUp() {
		this.plugin = new GuiMenuPlugin(pluginSet, mock(DataElementSP.class), mock(DocumentSP.class));
	}

	@Test
	public void getMenuItems() throws Exception {
		Collection<MenuItem> items = this.plugin.getMenuItems();
		assertThat("Wrong number of menu items received", items.size(), equalTo(1));
	}

	@Test
	public void setGetEnabled() throws Exception {
		assertThat("Plugin should be disabled", this.plugin.getEnabledProperty().getValue(), equalTo(false));

		this.plugin.getEnabledProperty().setValue(true);
		assertThat("Plugin should be enabled", this.plugin.getEnabledProperty().getValue(), equalTo(true));
	}

	@Test
	public void getWeight() throws Exception {
		assertThat("Wrong weight", this.plugin.getWeight(), equalTo(0));
	}

	@Test
	public void getPluginSet() throws Exception {
		assertThat("Wrong PluginSet", this.plugin.getPluginSet(), equalTo(this.pluginSet));
	}

}