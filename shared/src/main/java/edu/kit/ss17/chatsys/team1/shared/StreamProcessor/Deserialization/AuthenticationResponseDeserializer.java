package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationResponse;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import org.dom4j.Branch;
import org.jetbrains.annotations.Nullable;

/**
 * This class deserializes AuthenticationResponses.
 */
public class AuthenticationResponseDeserializer implements DeserializerInterface {

	private static final String SUCCESS = "success", FAILURE = "failure";

	@Nullable
	@Override
	public DataElementInterface deserializeXML(Branch branch) throws StanzaErrorException {
		AuthenticationResponse authenticationResponse = null;
		if (branch.node(0).getName().equals(SUCCESS)) {
			authenticationResponse = new AuthenticationResponse(true);
		} else if (branch.node(0).getName().equals(FAILURE)) {
			authenticationResponse = new AuthenticationResponse(false);
		}
		return authenticationResponse;
	}
}
