package edu.kit.ss17.chatsys.team1.shared.PluginManager;

import org.junit.*;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 *
 */
public class PluginManagerFactoryTest {

	private static final String PLUGINS_DIRECTORY = "plugins";
	private PluginManagerFactory factory;

	@BeforeClass
	public static void setUpClass() throws Exception {
		File basePath;
		try {
			basePath = Paths.get(PluginManagerFactory.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile();
		} catch (URISyntaxException ignored) {
			basePath = new File(PluginManagerFactory.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		}

		String path       = basePath.isDirectory() ? basePath.getPath() : basePath.getParent();
		Path   targetPath = Paths.get(path, PLUGINS_DIRECTORY);
		Path   sourcePath = FileSystems.getDefault().getPath(PLUGINS_DIRECTORY);
		Files.copy(sourcePath, targetPath);
		File sourceFile = new File(sourcePath.toString());
		for (final File file : sourceFile.listFiles()) {
			Files.copy(file.toPath(), Paths.get(targetPath.toString(), file.getName()));
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		File basePath;
		try {
			basePath = Paths.get(PluginManagerFactory.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile();
		} catch (URISyntaxException ignored) {
			basePath = new File(PluginManagerFactory.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		}

		String path       = basePath.isDirectory() ? basePath.getPath() : basePath.getParent();
		File   targetFile = Paths.get(path, PLUGINS_DIRECTORY).toFile();
		for (final File file : targetFile.listFiles()) {
			Files.delete(file.toPath());
		}
		Files.delete(targetFile.toPath());
	}

	@After
	public void tearDown() {
		this.factory.unload();
	}

	@Before
	public void setUp() {
		this.factory = PluginManagerFactory.getInstance();
	}

	@Test
	public void getInstance() throws Exception {
		// We expect getting a PluginManagerFactory with a list of plugins found in "plugins" folder.
		this.factory.scanPlugins();

		// Check plugins:
		Collection<PluginSetInterface> pluginSets = this.factory.getPluginSetList();

		Assert.assertTrue(pluginSets.size() == 1);

		boolean found = false;
		for (PluginSetInterface ps : pluginSets) {
			if (ps.getName() == "TCP")
				found = true;
		}

		Assert.assertTrue(found);
	}

	@Test
	public void enableDisablePluginSets() throws Exception {
		// We expect getting a PluginManagerFactory with a list of plugins found in "plugins" folder.
		this.factory.scanPlugins();

		// Check plugins:
		Collection<PluginSetInterface> pluginSets = this.factory.getPluginSetList();

		Assert.assertEquals(1, pluginSets.size());

		// Disable PluginSet
		// Since we haven't set a storage, we assume all plugins to be enabled
		Assert.assertEquals(1, this.factory.getEnabledPluginSets().size());

		for (PluginSetInterface ps : pluginSets) {
			((PluginManagerFactory) this.factory).disablePluginSet(ps);
		}

		Assert.assertEquals(0, this.factory.getEnabledPluginSets().size());

		// Enable PluginSet
		for (PluginSetInterface ps : pluginSets) {
			((PluginManagerFactory) this.factory).enablePluginSet(ps);
		}

		Collection<PluginSetInterface> enabled = this.factory.getEnabledPluginSets();

		Assert.assertEquals(1, this.factory.getEnabledPluginSets().size());
	}
}
