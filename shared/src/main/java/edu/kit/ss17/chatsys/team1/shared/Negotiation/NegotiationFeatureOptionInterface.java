package edu.kit.ss17.chatsys.team1.shared.Negotiation;

import java.util.Collection;

/**
 *
 */
public interface NegotiationFeatureOptionInterface {

	/**
	 * returns the name that identifies this option
	 */
	String getOptionName();

	/**
	 * returns all the supported Options for this option
	 */
	Collection<String> getValues();

	/**
	 * @param optionValue String add a supported choice for this option
	 */
	void addValue(String optionValue);
}
