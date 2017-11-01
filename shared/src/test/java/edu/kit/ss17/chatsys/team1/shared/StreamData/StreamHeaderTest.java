package edu.kit.ss17.chatsys.team1.shared.StreamData;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.StreamHeaderSerializerInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

/**
 *
 */
public class StreamHeaderTest {

	@Mock
	private StreamHeaderSerializerInterface streamHeaderSerializer;
	private StreamHeader streamHeader;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		StreamHeader.setSerializer(streamHeaderSerializer);
	}

	@Test
	public void serialize() {
		String                streamID     = "T3ST1D";
		String                from         = "test@example.com";
		String                to           = "example.com";
		String                namespace    = "jabber:client";
		StreamHeaderInterface streamHeader = new StreamHeader(streamID, from, to, namespace);
		Assert.assertThat(streamHeader.getStreamID(), sameInstance(streamID));
		Assert.assertThat(streamHeader.getFrom(), sameInstance(from));
		Assert.assertThat(streamHeader.getTo(), sameInstance(to));
		Assert.assertThat(streamHeader.getNamespace(), sameInstance(namespace));

		Assert.assertThat(StreamHeader.getSerializer(), is(streamHeaderSerializer));
		streamHeader.serialize();
		Mockito.verify(streamHeaderSerializer, times(1)).serialize(streamHeader);
	}

	@Test
	public void jidConstructor() throws InvalidJIDException {
		JID                   jid          = new JID("test@example.com");
		StreamHeaderInterface streamHeader = new StreamHeader(jid);
		Assert.assertThat(streamHeader.getNamespace(), is("jabber:client"));
		Assert.assertThat(streamHeader.getTo(), is(jid.getDomainPart()));
		Assert.assertThat(streamHeader.getFrom(), is(jid.getFullJID()));
		Assert.assertThat(streamHeader.getStreamID().isEmpty(), is(false));
	}

}
