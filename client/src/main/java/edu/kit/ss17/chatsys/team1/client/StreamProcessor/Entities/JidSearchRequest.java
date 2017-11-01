package edu.kit.ss17.chatsys.team1.client.StreamProcessor.Entities;

import edu.kit.ss17.chatsys.team1.shared.StreamData.IQ;
import edu.kit.ss17.chatsys.team1.shared.StreamData.IQInterface;

/**
 * Object representation of a JID-search-request-IQ.
 */
public class JidSearchRequest extends IQ implements JidSearchRequestInterface {

	private static final String REQUEST_NAME   = "jid-search";
	private static final String PARAMETER_NAME = "jid-fragment";
	private static final String REQUEST_TYPE   = "get";

	/**
	 * Constructor
	 * @param searchFragment the jid-fragment for which corresponding JIDs are searched.
	 */
	public JidSearchRequest(String searchFragment) {
		setRequest(REQUEST_NAME);
		addParameterName(PARAMETER_NAME);
		setType(REQUEST_TYPE);

		addParameterValue(searchFragment);
	}

	@Override
	public boolean isResponseMatching(IQInterface respose) {
		return respose.getRequest().equals(REQUEST_NAME) &&
		       respose.getParameterNames().contains(PARAMETER_NAME) &&
		       respose.getID().equals(this.getID());
	}

	@Override
	public String toString() {
		return "JidSearchRequest{} " + super.toString();
	}
}
