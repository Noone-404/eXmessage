package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationResponse;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

/**
 * Default Authentication Response Serializer
 */
public final class AuthenticationResponseSerializer implements AuthenticationResponseSerializerInterface {

	private static final String SUCCESS = "success", FAILURE = "failure";
	private static AuthenticationResponseSerializerInterface instance;

	/**
	 * Private constructor to make Serializers Singletons.
	 */
	private AuthenticationResponseSerializer() {
	}

	/**
	 * @return this Singleton's instance.
	 */
	public static AuthenticationResponseSerializerInterface getInstance() {
		return (instance != null) ? instance : (instance = new AuthenticationResponseSerializer());
	}

	@Override
	public Document serialize(DataElementInterface element) {
		if (!(element instanceof AuthenticationResponse)) {
			throw new IllegalArgumentException();
		}
		AuthenticationResponse authenticationResponse = (AuthenticationResponse) element;
		Document               document               = DocumentHelper.createDocument();
		if (authenticationResponse.getAuthenticationSuccess()) {
			document.addElement(SUCCESS);
		} else {
			document.addElement(FAILURE);
		}

		return document;
	}
}
