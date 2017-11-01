package edu.kit.ss17.chatsys.team1.shared.Negotiation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class NegotiationFeatureOptionTest {

	private NegotiationFeatureOptionInterface negotiationFeatureOption;

	@Before
	public void setUp() throws Exception {
		String             optionName   = "testOptionName";
		LinkedList<String> optionValues = new LinkedList<>();
		optionValues.add("firstTestValue");
		optionValues.add("anotherTestValue");
		negotiationFeatureOption = new NegotiationFeatureOption(optionName, optionValues);
	}

	@After
	public void tearDown() throws Exception {
		negotiationFeatureOption = null;
	}

	@Test
	public void getOptionName() throws Exception {
		String name = negotiationFeatureOption.getOptionName();
		assertTrue(name.equals("testOptionName"));
	}

	@Test
	public void getValues() throws Exception {
		Collection<String> values = negotiationFeatureOption.getValues();
		assertTrue(values.contains("firstTestValue") && values.contains("anotherTestValue"));
	}

	@Test
	public void equals() throws Exception {
		String             optionName   = "testOptionName";
		LinkedList<String> optionValues = new LinkedList<>();
		optionValues.add("firstTestValue");
		optionValues.add("anotherTestValue");
		NegotiationFeatureOptionInterface testOption = new NegotiationFeatureOption(optionName, optionValues);
		assertTrue(negotiationFeatureOption.equals(testOption));
	}

}
