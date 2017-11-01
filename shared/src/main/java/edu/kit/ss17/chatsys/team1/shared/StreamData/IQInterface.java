package edu.kit.ss17.chatsys.team1.shared.StreamData;

import java.util.Collection;

/**
 * IQInterface objects are used to represent client / server interactions.
 */
public interface IQInterface extends StanzaInterface {

	/**
	 * @return String request. E.g. "query"
	 */
	public String getRequest();

	/**
	 * @param request String request. E.g. "query"
	 */
	public void setRequest(String request);

	/**
	 * @return {@code Collection<String>} the parameter names of the request
	 */
	public Collection<String> getParameterNames();

	/**
	 * @param parameterName String add a parameter name to the parameter names of the request
	 */
	public void addParameterName(String parameterName);

	/**
	 * Clears the Collection of parameter names of the request.
	 */
	public void clearParameterNames();

	/**
	 * @return {@code Collection<String>} the values of the parameters from parameterNames (use getParameterNames to get those)
	 */
	public Collection<String> getParameterValues();

	/**
	 * @param parameterValue String the value of a parameter from parameterNames (use addParameterName to add those)
	 */
	public void addParameterValue(String parameterValue);

	/**
	 * Clears the Collection of parameter values;
	 */
	public void clearParameterValues();

	/**
	 * @return DataElementInterface the content of the IQ, e.g. the requested data from a get query.
	 */
	public DataElementInterface getContent();

	/**
	 * @param content DataElementInterface the content of the IQ, e.g. the requested data from a get query.
	 */
	public void setContent(DataElementInterface content);

	/**
	 * @return {@code Collection<String>} the default content of the IQ, i.e. Strings that get transmitted as plaintext .
	 */
	Collection<String> getDefaultContent();

	/**
	 * This method adds a contentString to the default content of the IQ.
	 * @param contentString String that get's added to the default content Collection.
	 */
	void addDefaultContent(String contentString);

	/**
	 * This method clears the default content Collection of the IQ.
	 */
	void clearDefaultContent();
}
