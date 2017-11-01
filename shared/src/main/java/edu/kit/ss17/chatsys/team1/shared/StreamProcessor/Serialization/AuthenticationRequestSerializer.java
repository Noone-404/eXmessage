package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationRequest;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Default Authentication Request Serializer
 */
public final class AuthenticationRequestSerializer implements AuthenticationRequestSerializerInterface {

	private static final String AUTHENTICATION = "auth", MECHANISM = "mechanism", JID = "jid", PASSWORD = "password";
	private static AuthenticationRequestSerializerInterface instance;

	/**
	 * Private constructor to make Serializers Singletons.
	 */
	private AuthenticationRequestSerializer() {
	}

	/**
	 * @return this Singleton's instance.
	 */
	public static AuthenticationRequestSerializerInterface getInstance() {
		return (instance != null) ? instance : (instance = new AuthenticationRequestSerializer());
	}

	@Override
	public Document serialize(DataElementInterface element) {
		if (!(element instanceof AuthenticationRequest)) {
			throw new IllegalArgumentException();
		}
		AuthenticationRequest authenticationRequest = (AuthenticationRequest) element;
		Document              document              = DocumentHelper.createDocument();
		Element               root                  = document.addElement(AUTHENTICATION);
		root.addAttribute(MECHANISM, authenticationRequest.getAuthMechanism());
		Element childJID = root.addElement(JID);
		childJID.addText(authenticationRequest.getJID().getFullJID());
		Element childPW = root.addElement(PASSWORD);
		childPW.addText(authenticationRequest.getPassword());
		return document;
	}
}
