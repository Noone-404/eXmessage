package edu.kit.ss17.chatsys.team1.shared.StreamData;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.AuthenticationRequestSerializerInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import org.dom4j.Document;

/**
 * Class to represent an authentication request for the simple password authentication containing JID and password
 */
public class AuthenticationRequest implements DataElementInterface {

	private final static String AUTH_MECHANISM = "simple-Password-Authentication";
	private static AuthenticationRequestSerializerInterface authenticationRequestSerializer;
	private JID    jid;
	private String password;

	/**
	 * constructor with JID and password for this authentication request
	 */
	public AuthenticationRequest(JID jid, String password) {
		this.jid = jid;
		this.password = password;
	}

	/**
	 * @return the serializer that is responsible for translating this object to a {@link Document}.
	 */
	public static AuthenticationRequestSerializerInterface getAuthenticationRequestSerializer() {
		return authenticationRequestSerializer;
	}

	/**
	 * @param authenticationRequestSerializer the serializer that is responsible for translating this object to a {@link Document}.
	 */
	public static void setAuthenticationRequestSerializer(AuthenticationRequestSerializerInterface authenticationRequestSerializer) {
		AuthenticationRequest.authenticationRequestSerializer = authenticationRequestSerializer;
	}

	/**
	 * returns a String that identifies the authentication mechanism used in this request
	 *
	 * @return the authentication mechanism
	 */
	public String getAuthMechanism() {
		return AUTH_MECHANISM;
	}

	/**
	 * returns the JID that shall be authenticated
	 */
	public JID getJID() {
		return this.jid;
	}

	/**
	 * returns the password sent with this authentication request
	 */
	public String getPassword() {
		return this.password;
	}

	@Override
	public Document serialize() {
		return authenticationRequestSerializer.serialize(this);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AuthenticationRequest) {
			AuthenticationRequest authenticationRequest = (AuthenticationRequest) o;
			return this.jid.equals(authenticationRequest.getJID()) && this.password.equals(authenticationRequest.getPassword());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime  = 31;
		int       result = 1;
		result = result * prime + this.jid.hashCode();
		result = result * prime + this.password.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "AuthenticationRequest{" +
		       "jid=" + jid +
		       ", password='" + password + '\'' +
		       '}';
	}
}
