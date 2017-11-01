package edu.kit.ss17.chatsys.team1.shared.Negotiation;

import java.util.Collection;

/**
 * Class to represent a single Option for a negotiation Feature
 */
public class NegotiationFeatureOption implements NegotiationFeatureOptionInterface {

	private String             optionName;
	private Collection<String> values;

	/**
	 * constructor
	 *
	 * @param name   name of this option
	 * @param values collection of the supported values for this option
	 */
	public NegotiationFeatureOption(String name, Collection<String> values) {
		this.optionName = name;
		this.values = values;
	}

	@Override
	public String getOptionName() {
		return optionName;
	}

	@Override
	public Collection<String> getValues() {
		return values;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NegotiationFeatureOption)) {
			return false;
		} else {
			NegotiationFeatureOption option = (NegotiationFeatureOption) o;
			return option.getOptionName() == this.getOptionName()
			       && option.getValues().containsAll(this.getValues()) && this.getValues().containsAll(option.getValues());
		}
	}

	@Override
	public int hashCode() {
		final int prime  = 31;
		int       result = 1;
		result = result * prime + this.optionName.hashCode();
		for (final String value : this.values) {
			result = result * prime + value.hashCode();
		}
		return result;
	}

	@Override
	public void addValue(String optionValue) {
		values.add(optionValue);
	}

	@Override
	public String toString() {
		return "NegotiationFeatureOption{" +
		       "optionName='" + optionName + '\'' +
		       ", values=" + values +
		       '}';
	}
}
