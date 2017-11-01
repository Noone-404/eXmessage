package edu.kit.ss17.chatsys.team1.shared.StreamData;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.MessageSerializerInterface;
import org.dom4j.Document;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Message.class})
public class MessageTest {

	private final String test = "Test.";
	@Mock
	private MessageObserverInterface messageObserverInterface;
	@Mock
	private MessageSerializerInterface messageSerializerInterface;
	private Message message;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Message.setMessageSerializer(messageSerializerInterface);
		message = new Message();
		message.registerObserver(messageObserverInterface);
	}

	@Test
	public void serialize() throws Exception {
		Assert.assertThat(Message.getMessageSerializer(), is(messageSerializerInterface));
		Document serializeXML = message.serialize();
		Mockito.verify(messageSerializerInterface, times(1)).serialize(message);
	}

	@Test
	public void setPlaintextRepresentation() throws Exception {
		message.setPlaintextRepresentation(test);
		assertTrue(message.getPlaintextRepresentation().equals(test));
		Mockito.verify(messageObserverInterface, times(1)).messageChanged(message);
	}

	@Test
	public void setExtendedContent() throws Exception {
		message.setExtendedContent(new Message());
		message.setExtendedContentXML("extended content");
		DataElementInterface dataElementInterface = message.getExtendedContent();
		Assert.assertThat(dataElementInterface instanceof MessageInterface, is(true));
		Assert.assertThat(message.getExtendedContentXML(), is("extended content"));
		Mockito.verify(messageObserverInterface, times(1)).messageChanged(message);
	}

	@Test
	public void testToString() throws Exception {
		message.setSender("sender");
		message.setReceiver("receiver");
		message.setID("id");
		message.setPlaintextRepresentation("plaintext");
		message.setExtendedContentXML("extended content");
		Assert.assertThat(message.toString(), is("Message{plaintext='plaintext', extendedContent=null, extendedContentXML='extended content'} AbstractStanza{id='id', " +
		                                         "sender='sender', receiver='receiver', clientSendDate=null, serverReceiveDate=null, clientReceiveDate=null, type=''}"));
	}

	@After
	public void tearDown() throws Exception {
		message.unregisterObserver(messageObserverInterface);
	}
}
