package edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin;

import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared.DataElementSP;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.*;

import java.io.File;
import java.nio.file.Files;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
@Ignore
public class DataElementSPTest {

	private static Document             correctXML;
	private static DataElementInterface element;

	@BeforeClass
	public static void setUp() throws Exception {
		correctXML = DocumentHelper.parseText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<message " + "id=\"ko2ba41c\" " + "from=\"bob@xmpp.example.org/mobile\" "
		                                      + "to=\"alice@xmpp.example.org\" " + "client-send-date=\"2017-07-04 13:47:05\" " + "server-receive-date=\"\" "
		                                      + "client-receive-date=\"\" " + "type=\"chat\">" + "<body>Das eXmessage-Programm funktioniert echt toll!<extendedContent" +
		                                      ">Test</extendedContent></body>" + "</message>");

		element = mock(DataElementInterface.class);
		when(element.serialize()).thenReturn(correctXML);
	}

	@AfterClass
	public static void cleanup() throws Exception {
		File file = new File("logs/DebugLoggerPlugin.log");
		Files.deleteIfExists(file.toPath());
	}

	@Test
	public void incomingDataElement() throws Exception {
		DataElementSP plugin = new DataElementSP(mock(PluginSetInterface.class));
		plugin.incomingDataElement(this.element);
		Assert.assertTrue(true);
	}

	@Test
	public void outgoingDataElement() throws Exception {
		DataElementSP plugin = new DataElementSP(mock(PluginSetInterface.class));
		plugin.outgoingDataElement(this.element);
		Assert.assertTrue(true);
	}

	@Test
	public void setGetEnabled() throws Exception {
		DataElementSP plugin = new DataElementSP(mock(PluginSetInterface.class));

		Assert.assertTrue(plugin.getEnabledProperty().getValue());

		plugin.getEnabledProperty().setValue(false);
		Assert.assertFalse(plugin.getEnabledProperty().getValue());
	}

	@Test
	public void getPluginSet() throws Exception {
		PluginSetInterface pluginSet = mock(PluginSetInterface.class);
		DataElementSP      plugin    = new DataElementSP(pluginSet);
		Assert.assertEquals(plugin.getPluginSet(), pluginSet);
	}
}
