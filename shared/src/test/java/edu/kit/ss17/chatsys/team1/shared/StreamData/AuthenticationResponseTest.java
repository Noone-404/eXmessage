package edu.kit.ss17.chatsys.team1.shared.StreamData;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.AuthenticationResponseSerializer;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.AuthenticationResponseSerializerInterface;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

/**
 *
 */
public class AuthenticationResponseTest {

	@Mock
	private AuthenticationResponseSerializerInterface authenticationResponseSerializer;

	@Test
	public void serialize() throws Exception {
		MockitoAnnotations.initMocks(this);
		AuthenticationResponse.setAuthenticationResponseSerializer(authenticationResponseSerializer);
		assertTrue(AuthenticationResponse.getAuthenticationResponseSerializer().equals(authenticationResponseSerializer));
		AuthenticationResponse authenticationResponse = new AuthenticationResponse(false);
		authenticationResponse.serialize();
		Mockito.verify(authenticationResponseSerializer, times(1)).serialize(authenticationResponse);
	}

	@Test
	public void getAuthenticationSuccess() throws Exception {
		AuthenticationResponse positiveAuthenticationResponse = new AuthenticationResponse(true);
		AuthenticationResponse negativeAuthenticationResponse = new AuthenticationResponse(false);
		assertTrue(positiveAuthenticationResponse.getAuthenticationSuccess());
		assertFalse(negativeAuthenticationResponse.getAuthenticationSuccess());
	}

	@Test
	public void equals() throws Exception {
		AuthenticationResponse authenticationResponse         = new AuthenticationResponse(true);
		AuthenticationResponse positiveAuthenticationResponse = new AuthenticationResponse(true);
		AuthenticationResponse negativeAuthenticationResponse = new AuthenticationResponse(false);
		DataElementInterface dataElementInterface = new Message();
		assertTrue(authenticationResponse.equals(authenticationResponse));
		assertTrue(authenticationResponse.equals(positiveAuthenticationResponse));
		assertFalse(authenticationResponse.equals(negativeAuthenticationResponse));
		assertFalse(authenticationResponse.equals(dataElementInterface));
	}

	@Test
	public void testHashcode() throws Exception {
		AuthenticationResponse authenticationResponse = new AuthenticationResponse(false);
		Assert.assertThat(authenticationResponse.toString(), is("AuthenticationResponse{success=false}"));
		Assert.assertThat(authenticationResponse.hashCode(), is(32));
	}
}
