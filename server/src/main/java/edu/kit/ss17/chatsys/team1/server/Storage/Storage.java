package edu.kit.ss17.chatsys.team1.server.Storage;

import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.Account;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

/**
 * Default Storage implementation.
 */
public class Storage implements StorageInterface {

	@NonNls
	private static final String  CONFIG_COMMENT = "eXmessage server config";
	private static final Logger  logger         = LogManager.getLogger(APP_NAME);
	private static final String  CONFIG_NAME    = "config.properties";
	private static final Pattern ACCOUNTS       = Pattern.compile("^user\\d+$");

	private static Storage    instance;
	private static Properties config;

	private Storage() {
		try {
			config = getFileConfig();
		} catch (IOException e) {
			throw new UncheckedIOException("Could not load storage", e);
		}
	}

	/**
	 * Gets the config file path.
	 *
	 * @return The path for the config file.
	 */
	@NotNull
	private static String getConfigPath() {
		File basePath;
		try {
			basePath = Paths.get(Storage.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile();
		} catch (URISyntaxException ignored) {
			basePath = new File(Storage.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		}

		String path = basePath.isDirectory() ? basePath.getPath() : basePath.getParent();

		return Paths.get(path, CONFIG_NAME).toFile().getPath();
	}

	public static Storage getInstance() {
		return instance != null ? instance : (instance = new Storage());
	}

	/**
	 * Get a configuration object with r/w access to our configuration file.
	 */
	private static Properties getFileConfig() throws IOException {
		// Create configuration file if it doesn't exist.
		File file = new File(getConfigPath());
		if (file.isDirectory())
			throw new IOException("Directory found where file expected");

		if (file.exists())
			logger.debug("Loading config file '" + file.getAbsolutePath() + '\'');
		else
			createDefaultConfig(file);

		Properties properties = new Properties();
		try (FileInputStream inputStream = new FileInputStream(file)) {
			properties.load(inputStream);
		}

		return properties;
	}

	private static void createDefaultConfig(File file) throws IOException {
		logger.debug("Creating default config file '" + file.getAbsolutePath() + '\'');
		if (file.getParentFile() != null)
			file.getParentFile().mkdirs();
		Properties properties = new Properties();
		// properties.setProperty("port", Integer.toString(DEFAULT_PORT)); // TODO: Server Listening-Port aus der Config laden & verwenden

		try (FileOutputStream outputStream = new FileOutputStream(file)) {
			properties.store(outputStream, CONFIG_COMMENT);
		}
	}

	private static void saveConfig() {
		try (FileOutputStream outputStream = new FileOutputStream(getConfigPath())) {
			config.store(outputStream, CONFIG_COMMENT);
		} catch (IOException e) {
			throw new UncheckedIOException("Could not save config", e);
		}
	}

	/**
	 * @param account the account
	 */
	@Override
	public void saveAccount(AccountInterface account) {
		// Get all saved accounts. If our accounts JID is included, update it. Otherwise add it.

		boolean         createEntry = true;
		HashSet<String> foundKeys   = new HashSet<>();
		for (Object key : config.keySet()) {
			if (!ACCOUNTS.matcher(key.toString()).matches())
				continue;

			foundKeys.add(key.toString());

			String[] parts = config.getProperty(key.toString()).split("@", 3); // parts[2] is PW
			if (parts.length < 2)
				continue;

			String jid = parts[0] + '@' + parts[1];

			if (!jid.equals(account.getJid().getBaseJID()))
				continue;

			config.setProperty(key.toString(), jid + '@' + account.getPassword());
			createEntry = false;
			break;
		}

		if (createEntry) {
			int index = 0;
			while (foundKeys.contains("user" + index))
				index++;

			config.setProperty("user" + index, account.getJid().getBaseJID() + '@' + account.getPassword());
		}

		saveConfig();
	}

	@Override
	public void removeAccount(AccountInterface account) {
		for (Object key : config.keySet()) {
			if (!ACCOUNTS.matcher(key.toString()).matches())
				continue;

			String[] parts = config.getProperty(key.toString()).split("@", 3); // parts[2] is PW
			if (parts.length < 2)
				continue;

			String jid = parts[0] + '@' + parts[1];

			if (!jid.equals(account.getJid().getBaseJID()))
				continue;

			config.remove(key);
			saveConfig();
			break;
		}
	}

	@Nullable
	@Override
	public AccountInterface getAccount(JID jid) {
		for (Object key : config.keySet()) {
			if (!ACCOUNTS.matcher(key.toString()).matches())
				continue;

			String[] parts = config.getProperty(key.toString()).split("@", 3); // parts[2] is PW
			if (parts.length < 2)
				continue;

			String storedJid = parts[0] + '@' + parts[1];

			if (!storedJid.equals(jid.getBaseJID()))
				continue;

			return new Account(jid, parts[2]);
		}

		return null;
	}

	@Override
	public Collection<JID> getAllAccountJids() {
		return config
				.keySet()
				.stream()
				.map(Object::toString)
				.filter(key -> ACCOUNTS.matcher(key).matches())
				.map(config::getProperty)
				.map(s -> s.split("@", 3))
				.filter(arr -> arr.length >= 2)
				.map(arr -> {
					try {
						return new JID(arr[0] + '@' + arr[1]);
					} catch (InvalidJIDException ignored) {
						//noinspection ReturnOfNull
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
}
