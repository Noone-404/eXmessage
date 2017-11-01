package edu.kit.ss17.chatsys.team1.shared.PluginManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 * Default implementation of a PluginManager.
 */
public class PluginManager implements PluginManagerInterface {

	private static final Logger logger = LogManager.getLogger(APP_NAME);

	private final Object                          clearPluginablesLock = new Object();
	private final Collection<PluginableInterface> allPluginables       = new ArrayList<>();
	private final Collection<PluginableInterface> pluginables          = new ArrayList<>();
	private final Map<String, PluginSetInterface> pluginSets           = new HashMap<>();

	public PluginManager(Collection<PluginSetInterface> pluginSets) {
		for (PluginSetInterface psi : pluginSets)
			this.pluginSets.put(psi.getName(), psi);
	}

	@Override
	public Collection<PluginSetInterface> getPluginSets() {
		return this.pluginSets.values();
	}

	@Override
	public void registerPluginable(PluginableInterface pluginable) {
		synchronized (this.clearPluginablesLock) {
			if (!this.allPluginables.contains(pluginable)) {
				this.allPluginables.add(pluginable);
				this.pluginables.add(pluginable);
			}
		}
	}

	@Override
	public void registerPlugins() {
		List<PluginableInterface> tmpPluginables;

		synchronized (this.clearPluginablesLock) {
			tmpPluginables = new ArrayList<>(this.pluginables);
			this.pluginables.clear();
		}

		List<PluginSetInterface> sets    = new ArrayList<>(getPluginSets());
		int                      numSets = sets.size();

		for (int i = 0; i < numSets; i++) {
			boolean lastSet = (i + 1) == numSets;

			PluginSetInterface    psi        = sets.get(i);
			List<PluginInterface> plugins    = new ArrayList<>(psi.getPlugins());
			int                   numPlugins = plugins.size();

			for (int j = 0; j < numPlugins; j++) {
				boolean lastPlugin = (j + 1) == numPlugins;

				for (PluginableInterface pluginable : tmpPluginables) {
					logger.debug("Registering plugin '" + plugins.get(j).getClass().getSimpleName() + "' at pluginable '" + pluginable.getClass().getSimpleName() + '\'');
					pluginable.tryRegisterPlugin(plugins.get(j), lastSet && lastPlugin);
				}
			}
		}
	}
}
