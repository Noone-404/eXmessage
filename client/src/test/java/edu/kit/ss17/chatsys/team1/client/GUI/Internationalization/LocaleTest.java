package edu.kit.ss17.chatsys.team1.client.GUI.Internationalization;

import edu.kit.ss17.chatsys.team1.client.Storage.Storage;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.ResourceBundle;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class LocaleTest {

	@BeforeClass
	public static void setUp() {
		Storage storage = Mockito.mock(Storage.class);
		Mockito.when(storage.getLanguage()).thenReturn("en");
		Locale.setStorage(storage);
	}

	private static FeatureMatcher<LocaleInterface, Boolean> current(Matcher<Boolean> matcher) {
		return new FeatureMatcher<LocaleInterface, Boolean>(matcher, "isCurrent", "current") {
			@Override
			protected Boolean featureValueOf(LocaleInterface t) {
				return t.isCurrent();
			}
		};
	}

	@Test
	public void getCurrent() {
		assertThat("Locale returned by getCurrent has to be the current one", Locale.getCurrent(), current(is(true)));
	}

	@Test
	public void getAll() {
		assertThat("GetAll must not be empty", Locale.getAll(), is(not(empty())));
		assertThat("GetAll must contains current element", Locale.getAll(), hasItem(current(equalTo(true))));
	}

	@Test
	public void makeCurrent() {
		List<LocaleInterface> all = Locale.getAll();
		if (all.size() < 2) // Cannot execute test - needs at least two different locale objects
			return; // There is no "warning" functionality in JUnit, thus simply return.

		LocaleInterface first  = all.get(0);
		LocaleInterface second = all.get(1);

		LocaleInterface newCurrent, nonCurrent;
		if (first.isCurrent()) {
			second.makeCurrent();
			newCurrent = second;
			nonCurrent = first;
		} else {
			first.makeCurrent();
			newCurrent = first;
			nonCurrent = second;
		}

		assertThat("New locale has to be current after makeCurrent", newCurrent, current(is(true)));
		assertThat("Old current locale must not be current", nonCurrent, current(is(false)));

		nonCurrent.makeCurrent();
		assertThat("Old current locale must be current again", nonCurrent, current(is(true)));
	}

	@Test
	public void equality() {
		LocaleInterface current = Locale.getCurrent();

		assertThat("Current must not be null", current, is(not(nullValue())));
		assertThat("Current must not equal null", current, is(not(equalTo(nullValue()))));
		assertThat("Current has to equal another current object", current, is(equalTo(Locale.getCurrent())));
		assertThat("Current has to equal itself", current, is(equalTo(current)));
	}

	@Test
	public void getBundle() {
		assertThat("Have to get valid resource bundle", Locale.getCurrent(true).getBundle(), instanceOf(ResourceBundle.class));
	}

}
