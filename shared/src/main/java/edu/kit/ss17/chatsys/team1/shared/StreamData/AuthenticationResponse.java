package edu.kit.ss17.chatsys.team1.shared.StreamData;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.AuthenticationResponseSerializerInterface;
import org.dom4j.Document;

/**
 * Class to represent the response to an authentication request
 */
public class AuthenticationResponse implements DataElementInterface {

	private static AuthenticationResponseSerializerInterface authenticationResponseSerializer;

	private boolean success;

	/**
	 * constructor with parameter to indicate whether this is a successful authentication response
	 */
	public AuthenticationResponse(boolean success) {
		this.success = success;
	}

	/**
	 * @return the serializer that is responsible for translating this object to a {@link Document}.
	 */
	public static AuthenticationResponseSerializerInterface getAuthenticationResponseSerializer() {
		return authenticationResponseSerializer;
	}

	/**
	 * @param authenticationResponseSerializer the serializer that is responsible for translating this object to a {@link Document}.
	 */
	public static void setAuthenticationResponseSerializer(
			AuthenticationResponseSerializerInterface authenticationResponseSerializer) {
		AuthenticationResponse.authenticationResponseSerializer = authenticationResponseSerializer;
	}

	/**
	 * Indicates whether this is representing a response that indicates a successful authentication
	 */
	public boolean getAuthenticationSuccess() {
		return this.success;
	}

	@Override
	public Document serialize() {
		return authenticationResponseSerializer.serialize(this);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AuthenticationResponse) {
			AuthenticationResponse authenticationResponse = (AuthenticationResponse) o;
			return this.success == authenticationResponse.getAuthenticationSuccess();
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime  = 31;
		int       result = 1;
		result = result * prime + (this.success ? 0 : 1);
		return result;
	}

	@Override
	public String toString() {
		return "AuthenticationResponse{" +
		       "success=" + success +
		       '}';
	}
}
