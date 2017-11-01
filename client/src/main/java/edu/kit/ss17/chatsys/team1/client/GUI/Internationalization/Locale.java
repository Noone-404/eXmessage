package edu.kit.ss17.chatsys.team1.client.GUI.Internationalization;

import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Locale class is responsible for managing localisation resources.
 */
public final class Locale implements LocaleInterface {

	/**
	 * Name of the internal resource containing the default locale.
	 */
	private static final String DEFAULT_RES         = "default-locale";
	/**
	 * Default language tag.
	 */
	private static final String DEFAULT_LOCALE_NAME = "en";

	/**
	 * File extension of locale files used at runtime.
	 */
	private static final String  LOCALE_FILE_EXTENSION = "properties";
	/**
	 * Resource bundle name of locale resource bundle used at runtime.
	 */
	private static final String  LOCALE_BUNDLE_NAME    = "locale";
	/**
	 * Subdirectory relative to the current executable directory in which the locales are to be found.
	 */
	private static final String  LOCALE_DIRECTORY      = LOCALE_BUNDLE_NAME;
	/**
	 * Pattern for valid locale files. Note that this pattern does not restrict the class loader used for loading the resources - rather this pattern resembles the format that the
	 * class loader recognizes.
	 */
	@SuppressWarnings("HardcodedFileSeparator") // ... it is NOT a file separator.
	private static final Pattern LOCALE_FILE           = Pattern.compile('^' + LOCALE_BUNDLE_NAME + "((_[a-zA-Z]+)*)\\." + LOCALE_FILE_EXTENSION + '$');

	/**
	 * Shared lock used to synchronize access to the current chosen locale.
	 */
	private static final Object lock = new Object();
	/**
	 * Caches the URI describing the directory in which the locales are to be found.
	 */
	@SuppressWarnings("ConstantNamingConvention")
	private static final URL              localeUrl;
	/**
	 * A list containing all found locale file names.
	 */
	@SuppressWarnings("ConstantNamingConvention")
	private static final List<String>     allLocaleFileNames;
	/**
	 * Caches the current chosen locale identifier.
	 */
	private static       String           activeLocaleIdentifier;
	/**
	 * Reference to storage.
	 */
	private static       StorageInterface storage;

	static {
		Path path = Paths.get(getLocaleDirectory());
		path.toFile().mkdirs(); // does nothing if directory already existent

		try {
			localeUrl = path.toUri().toURL();
			try (Stream<Path> allPaths = Files.walk(path)) {
				allLocaleFileNames = allPaths.filter(Files::isRegularFile).map(Path::toFile).filter(Locale::isValidLocaleFile).map(File::getName).collect(Collectors.toList());

				if (allLocaleFileNames.stream().map(Locale::getLocalePart).noneMatch(String::isEmpty))
					try (InputStream is = Locale.class.getResourceAsStream(DEFAULT_RES)) {
						Path target = Paths.get(getLocaleDirectory(), LOCALE_BUNDLE_NAME + '.' + LOCALE_FILE_EXTENSION);
						Files.copy(is, target);
						allLocaleFileNames.add(target.toFile().getName()); // default/fallback locale
					}

				if (allLocaleFileNames.stream().map(Locale::getLocalePart).noneMatch(s -> s.equals(DEFAULT_LOCALE_NAME))) {
					File defaultLocale = Paths.get(getLocaleDirectory(), LOCALE_BUNDLE_NAME + '_' + DEFAULT_LOCALE_NAME + '.' + LOCALE_FILE_EXTENSION).toFile();
					defaultLocale.createNewFile();
					allLocaleFileNames.add(defaultLocale.getName()); // proxy-locale
				}
			}

			java.util.Locale.setDefault(java.util.Locale.forLanguageTag(DEFAULT_LOCALE_NAME));
		} catch (IOException e) { // cannot read/create locale
			// no point trying anything else, the program would break anyway when trying to get internationalized strings because of missing files
			throw new UncheckedIOException(e);
		}
	}

	private final String identifier;
	private final String displayName;

	/**
	 * Creates a new Locale object using the provided identifier.
	 *
	 * @param identifier The identifier to use for the Locale object. If empty or null the default locale will be used.
	 */
	private Locale(String identifier) {
		if (identifier == null || identifier.isEmpty())
			identifier = DEFAULT_LOCALE_NAME;

		this.identifier = identifier;
		this.displayName = java.util.Locale.forLanguageTag(identifier).getDisplayName(java.util.Locale.forLanguageTag(identifier));
	}

	/**
	 * Checks if the name of a provided file is valid to use as a locale file.
	 *
	 * @param file The file to check.
	 *
	 * @return Returns true if the filename is valid.
	 */
	private static boolean isValidLocaleFile(File file) {
		return LOCALE_FILE.matcher(file.getName()).matches();
	}

	/**
	 * Gets the locale part of a filename.
	 *
	 * @param name The filename to get the locale part from.
	 *
	 * @return Returns the locale part or an empty string, if the locale part could not be found.
	 */
	private static String getLocalePart(String name) {
		Matcher matcher = LOCALE_FILE.matcher(name);
		return (matcher.find() && (matcher.groupCount() >= 1)) ? ((matcher.group(1).length() >= 1) ? matcher.group(1).substring(1) : matcher.group(1)) : "";
	}

	/**
	 * Gets the current locale identifier. If the identifier has not jet been cached, if is fetched from the data storage and saved in the cache.
	 *
	 * @return Returns the current locale identifier.
	 */
	private static String getCurrentIdentifier() {
		synchronized (lock) {
			if (activeLocaleIdentifier == null && storage != null)
				activeLocaleIdentifier = storage.getLanguage();

			return activeLocaleIdentifier;
		}
	}

	/**
	 * Gets the directory in which the locale files are to be found.
	 *
	 * @return The directory path for the locale files.
	 */
	@NotNull
	private static String getLocaleDirectory() {
		File basePath;
		try {
			basePath = Paths.get(Locale.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toFile();
		} catch (URISyntaxException ignored) {
			basePath = new File(Locale.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		}

		String path = basePath.isDirectory() ? basePath.getPath() : basePath.getParent();

		return Paths.get(path, LOCALE_DIRECTORY).toFile().getPath();
	}

	/**
	 * Gets the current chosen locale.
	 *
	 * @param validateName If set to true, the currently stored value will be checked for validity. In case the appropriate locale file is missing, the currently stored locale
	 *                     identifier will be updated to correspond to the default locale.
	 *
	 * @return Gets a Locale object that corresponds to the currently chosen locale.
	 */
	@NotNull
	public static LocaleInterface getCurrent(boolean validateName) {
		if (validateName)
			getAll(); // discard result. Only needed for validation check (may update current locale)

		return getCurrent();
	}

	/**
	 * Gets the current chosen locale without performing any validity checks.
	 *
	 * @return Gets a Locale object that corresponds th the currently chosen locale.
	 */
	@NotNull
	public static LocaleInterface getCurrent() {
		return new Locale(getCurrentIdentifier());
	}

	/**
	 * Initializes the storage
	 *
	 * @param storage The storage object to read the initial locale / save the current locale
	 */
	public static void setStorage(StorageInterface storage) {
		Locale.storage = storage;
	}

	/**
	 * Returns a list of all found locales. This list is guaranteed to contain at least a single element (the default locale). It is further ensured that at least one element in
	 * this list will be the currently selected one.
	 *
	 * @return A list of available locales.
	 */
	public static List<LocaleInterface> getAll() {
		List<LocaleInterface>     locales       = allLocaleFileNames.stream().map(Locale::getLocalePart).map(Locale::new).distinct().collect(Collectors.toList());
		Optional<LocaleInterface> currentInList = locales.stream().filter(locale -> locale.getIdentifier().equals(getCurrentIdentifier())).findFirst();

		if (!currentInList.isPresent())
			new Locale(DEFAULT_LOCALE_NAME).makeCurrent();

		return locales;
	}

	@Override
	public ResourceBundle getBundle() {
		try (URLClassLoader resourceClassLoader = new URLClassLoader(new URL[] {localeUrl})) {
			return ResourceBundle.getBundle(LOCALE_BUNDLE_NAME, java.util.Locale.forLanguageTag(getIdentifier()), resourceClassLoader);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Contract(pure = true)
	@Override
	public String getIdentifier() {
		return this.identifier;
	}

	@Contract(pure = true)
	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public void makeCurrent() {
		if (!getIdentifier().equals(getCurrentIdentifier())) {
			synchronized (lock) {
				if (storage != null)
					storage.saveLanguage(this.identifier);

				activeLocaleIdentifier = this.identifier;
			}
		}
	}

	@Override
	public boolean isCurrent() {
		return getIdentifier().equals(getCurrentIdentifier());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		Locale locale = (Locale) obj;

		return this.identifier.equals(locale.identifier);
	}

	@Override
	public int hashCode() {
		return this.identifier.hashCode();
	}
}
