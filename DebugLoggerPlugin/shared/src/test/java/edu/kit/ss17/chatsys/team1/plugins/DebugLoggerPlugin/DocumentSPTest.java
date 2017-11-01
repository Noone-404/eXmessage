package edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin;

import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared.DocumentSP;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.*;

import java.io.File;
import java.nio.file.Files;

import static org.mockito.Mockito.mock;

/**
 *
 */
@Ignore
public class DocumentSPTest {

	private static Document correctXML;

	@BeforeClass
	public static void setUp() throws Exception {
		correctXML = DocumentHelper.parseText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<message " + "id=\"ko2ba41c\" " + "from=\"bob@xmpp.example.org/mobile\" "
		                                      + "to=\"alice@xmpp.example.org\" " + "client-send-date=\"2017-07-04 13:47:05\" " + "server-receive-date=\"\" "
		                                      + "client-receive-date=\"\" " + "type=\"chat\">" + "<body>Das eXmessage-Programm funktioniert echt toll!<extendedContent" +
		                                      ">Test</extendedContent></body>" + "</message>");
	}

	@AfterClass
	public static void cleanup() throws Exception {
		File file = new File("logs/DebugLoggerPlugin.log");
		Files.deleteIfExists(file.toPath());
	}

	@Test
	public void incomingDocument() throws Exception {
		DocumentSP plugin = new DocumentSP(mock(PluginSetInterface.class));
		plugin.incomingDocument(correctXML);
	}

	@Test
	public void outgoingDocument() throws Exception {
		DocumentSP plugin = new DocumentSP(mock(PluginSetInterface.class));
		plugin.outgoingDocument(correctXML);
	}

	@Test
	public void setGetEnabled() throws Exception {
		DocumentSP plugin = new DocumentSP(mock(PluginSetInterface.class));

		Assert.assertTrue(plugin.getEnabledProperty().getValue());

		plugin.getEnabledProperty().setValue(false);
		Assert.assertFalse(plugin.getEnabledProperty().getValue());
	}

	@Test
	public void getPluginSet() throws Exception {
		PluginSetInterface pluginSet = mock(PluginSetInterface.class);
		DocumentSP         plugin    = new DocumentSP(pluginSet);
		Assert.assertEquals(plugin.getPluginSet(), pluginSet);
	}

}
