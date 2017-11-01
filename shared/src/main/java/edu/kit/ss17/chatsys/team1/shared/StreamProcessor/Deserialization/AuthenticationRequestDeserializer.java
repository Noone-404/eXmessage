package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationRequest;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Constants;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StreamErrorException;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * This class deserializes AuthenticationRequests.
 */
public class AuthenticationRequestDeserializer implements DeserializerInterface {

	private static final String AUTHENTICATION = "auth", JID = "jid", PASSWORD = "password";

	@Nullable
	@Override
	public DataElementInterface deserializeXML(Branch branch) throws StreamErrorException {
		DataElementInterface authenticationRequest = null;
		Element              root                  = ((Document) branch).getRootElement();
		if (root == null) {
			throw new StreamErrorException(StreamErrorCondition.BAD_FORMAT, Constants.EMPTY_XML_MESSAGE);
		}
		if (root.getName().equals(AUTHENTICATION)) {
			String jid      = null;
			String password = null;
			for (final Element child : (Collection<Element>) root.elements()) {
				if (child.getName().equals(JID)) {
					jid = child.getText();
				} else if (child.getName().equals(PASSWORD)) {
					password = child.getText();
				}
			}
			if (jid == null) {
				throw new StreamErrorException(StreamErrorCondition.BAD_FORMAT, "JID missing from authentication request.");
			} else if (password == null) {
				throw new StreamErrorException(StreamErrorCondition.BAD_FORMAT, "Password missing from authentication request.");
			}
			try {
				authenticationRequest = new AuthenticationRequest(new JID(jid), password);
			} catch (InvalidJIDException ignored) {
				throw new StreamErrorException(StreamErrorCondition.INVALID_FROM, "Invalid JID in authentication request.");
			}
		}
		return authenticationRequest;
	}
}
