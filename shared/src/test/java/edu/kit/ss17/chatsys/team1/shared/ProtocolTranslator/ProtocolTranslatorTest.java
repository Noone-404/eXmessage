package edu.kit.ss17.chatsys.team1.shared.ProtocolTranslator;


import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors.StreamProtocolError;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainLowerDataProcessorInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainUpperDataProcessorInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ClosingTagElement;
import edu.kit.ss17.chatsys.team1.shared.Util.OpeningTagElement;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 *
 */
public class ProtocolTranslatorTest {

	private static final Charset CHARSET = Charset.forName("UTF-8");
	private ProtocolTranslatorInterface                protocolTranslator;
	private ChainLowerDataProcessorInterface<byte[]>   lower;
	private ChainUpperDataProcessorInterface<Document> upper;
	@Captor
	private ArgumentCaptor<Document> upperCaptor = ArgumentCaptor.forClass(Document.class);

	@Captor
	private ArgumentCaptor<byte[]> lowerCaptor = ArgumentCaptor.forClass(byte[].class);

	@Captor
	private ArgumentCaptor<ProtocolErrorInterface> firstErrorCaptor = ArgumentCaptor.forClass(ProtocolErrorInterface.class);

	@Captor
	private ArgumentCaptor<ProtocolErrorInterface> secondErrorCaptor = ArgumentCaptor.forClass(ProtocolErrorInterface.class);

	@Before
	public void setUp() {
		this.protocolTranslator = new ProtocolTranslator();
		this.upper = mock(ChainUpperDataProcessorInterface.class);
		this.lower = mock(ChainLowerDataProcessorInterface.class);
		this.protocolTranslator.setLower(this.lower);
		this.protocolTranslator.setUpper(this.upper);
	}

	@After
	public void tearDown() {
		this.protocolTranslator = null;
		this.lower = null;
		this.upper = null;
	}

	@Test
	public void pushDownStanza() throws Exception {
		Document messageDocument = DocumentHelper.createDocument();
		Element  root            = messageDocument.addElement("message");
		root.addAttribute("from", "test@example.com/resource");
		root.addAttribute("to", "anothertest@example.com");
		Element body = root.addElement("body");
		body.addText("This is a test message");
		this.protocolTranslator.pushDataDown(messageDocument);
		verify(this.lower).pushDataDown(this.lowerCaptor.capture());
		byte[] sentBytes = this.lowerCaptor.getValue();
		assertArrayEquals(root.asXML().getBytes(CHARSET), sentBytes);
	}

	@Test
	public void pushStanzaUp() throws Exception {
		Document messageDocument = DocumentHelper.createDocument();
		Element  root            = messageDocument.addElement("message");
		root.addAttribute("from", "test@example.com/resource");
		root.addAttribute("to", "anothertest@example.com");
		Element body = root.addElement("body");
		body.addText("This is a test message");
		this.protocolTranslator.pushDataUp(root.asXML().getBytes());
		verify(this.upper).pushDataUp(this.upperCaptor.capture());
		Document document = this.upperCaptor.getValue();
		assertEquals(root.asXML(), document.getRootElement().asXML());
	}

	@Test
	public void pushStanzaUpWithIncompleteNext() throws Exception {
		Document messageDocument = DocumentHelper.createDocument();
		Element  root            = messageDocument.addElement("message");
		root.addAttribute("from", "test@example.com/resource");
		root.addAttribute("to", "anothertest@example.com");
		Element body = root.addElement("body");
		body.addText("This is a test message");
		String receivedString = root.asXML() + "<presence><";
		this.protocolTranslator.pushDataUp(receivedString.getBytes());
		verify(this.upper).pushDataUp(this.upperCaptor.capture());
		Document document = this.upperCaptor.getValue();
		assertEquals(root.asXML(), document.getRootElement().asXML());
	}

	@Test
	public void pushStanzasUpInSteps() throws Exception {
		Document messageDocument = DocumentHelper.createDocument();
		Element  root            = messageDocument.addElement("message");
		root.addAttribute("from", "test@example.com/resource");
		root.addAttribute("to", "anothertest@example.com");
		Element body = root.addElement("body");
		body.addText("This is a test message");
		String receivedString = root.asXML() + "<presence from=\"test@example.com\"><";
		this.protocolTranslator.pushDataUp(receivedString.getBytes(CHARSET));
		String secondString = "show>dnd</show><status>Status message test</status></presence>";
		this.protocolTranslator.pushDataUp(secondString.getBytes(CHARSET));
		verify(this.upper, times(2)).pushDataUp(this.upperCaptor.capture());
		List<Document> documents = this.upperCaptor.getAllValues();
		assertEquals(root.asXML(), ((Document) documents.toArray()[0]).getRootElement().asXML());
		assertEquals("<presence from=\"test@example.com\"><" + secondString, ((Document) documents.toArray()[1]).getRootElement().asXML());
	}

	@Test
	public void pushStreamHeaderDown() throws Exception {
		OpeningTagElement openingTagElement = new OpeningTagElement("stream");
		openingTagElement.addAttribute("from", "test@example.com");
		openingTagElement.addAttribute("to", "example.com");
		openingTagElement.setNamespace(new Namespace(null, "jabber:client"));
		Document document = DocumentHelper.createDocument(openingTagElement);
		this.protocolTranslator.pushDataDown(document);
		verify(this.lower).pushDataDown(this.lowerCaptor.capture());
		byte[] bytes = this.lowerCaptor.getValue();
		assertArrayEquals(openingTagElement.asXML().getBytes(CHARSET), bytes);
	}

	@Test
	public void pushStreamHeaderUp() throws Exception {
		OpeningTagElement openingTagElement = new OpeningTagElement("stream");
		openingTagElement.addAttribute("from", "test@example.com");
		openingTagElement.addAttribute("to", "example.com");
		openingTagElement.setNamespace(new Namespace(null, "jabber:client"));
		this.protocolTranslator.pushDataUp(openingTagElement.asXML().getBytes(CHARSET));
		verify(this.upper).pushDataUp(this.upperCaptor.capture());
		Document document = this.upperCaptor.getValue();
		assertEquals(openingTagElement.asXML(), document.getRootElement().asXML());
	}

	@Test
	public void pushStreamFooterDown() throws Exception {
		ClosingTagElement closingTagElement = new ClosingTagElement("stream");
		Document          document          = DocumentHelper.createDocument(closingTagElement);
		this.protocolTranslator.pushDataDown(document);
		verify(this.lower).pushDataDown(this.lowerCaptor.capture());
		byte[] bytes = this.lowerCaptor.getValue();
		assertArrayEquals(closingTagElement.asXML().getBytes(CHARSET), bytes);
	}

	@Test
	public void pushStreamFooterUp() throws Exception {
		ClosingTagElement closingTagElement = new ClosingTagElement("stream");
		this.protocolTranslator.pushDataUp(closingTagElement.asXML().getBytes(CHARSET));
		verify(this.upper).pushDataUp(this.upperCaptor.capture());
		Document document = this.upperCaptor.getValue();
		assertEquals(closingTagElement.asXML(), document.getRootElement().asXML());
	}

	@Test
	public void pushUpStreamHeaderAndStanza() throws Exception {
		OpeningTagElement openingTagElement = new OpeningTagElement("stream");
		openingTagElement.addAttribute("from", "test@example.com");
		openingTagElement.addAttribute("to", "example.com");
		openingTagElement.setNamespace(new Namespace(null, "jabber:client"));
		String receivedString = openingTagElement.asXML() + "<presence from=\"test@example.com\"><";
		this.protocolTranslator.pushDataUp(receivedString.getBytes(CHARSET));
		String secondString = "show>dnd</show><status>Status message test</status></presence>";
		this.protocolTranslator.pushDataUp(secondString.getBytes(CHARSET));
		verify(this.upper, times(2)).pushDataUp(this.upperCaptor.capture());
		List<Document> documents = this.upperCaptor.getAllValues();
		assertEquals(openingTagElement.asXML(), ((Document) documents.toArray()[0]).getRootElement().asXML());
		assertEquals("<presence from=\"test@example.com\"><" + secondString, ((Document) documents.toArray()[1]).getRootElement().asXML());
	}

	@Test
	public void pushUpMalformedStreamTag() throws Exception {
		ProtocolErrorObserverInterface firstObserver      = mock(ProtocolErrorObserverInterface.class);
		ProtocolErrorObserverInterface secondObserver     = mock(ProtocolErrorObserverInterface.class);
		String                         malformedStreamTag = "<stream from=\"incompl>";
		this.protocolTranslator.registerErrorObserver(firstObserver);
		this.protocolTranslator.registerErrorObserver(secondObserver);
		this.protocolTranslator.pushDataUp(malformedStreamTag.getBytes(CHARSET));
		this.protocolTranslator.unregisterErrorObserver(firstObserver);
		this.protocolTranslator.pushDataUp(malformedStreamTag.getBytes(CHARSET));
		verify(firstObserver, times(1)).onProtocolError(this.firstErrorCaptor.capture());
		Collection<ProtocolErrorInterface> protocolErrors = this.firstErrorCaptor.getAllValues();
		verify(secondObserver, times(2)).onProtocolError(this.secondErrorCaptor.capture());
		protocolErrors.addAll(this.secondErrorCaptor.getAllValues());
		for (final ProtocolErrorInterface currentError : protocolErrors) {
			assertTrue(currentError.isFatal());
			if (currentError instanceof StreamProtocolError) {
				StreamProtocolError firstStreamError = (StreamProtocolError) currentError;
				assertEquals(StreamErrorCondition.BAD_FORMAT, firstStreamError.getCondition());
			} else {
				fail("Error Element is not a Stream Error");
			}
		}
	}

	@Test
	public void pushUpIncompleteStanza() throws Exception {
		String incompleteMessage = "<message from=\"test@example.com to=\"bla";
		this.protocolTranslator.pushDataUp(incompleteMessage.getBytes(CHARSET));
		verify(this.upper, never()).pushDataUp(this.upperCaptor.capture());
	}

	@Test
	public void pushUpMultipleStanzas() {
		Document messageDocument = DocumentHelper.createDocument();
		Element  root            = messageDocument.addElement("message");
		root.addAttribute("from", "test@example.com/resource");
		root.addAttribute("to", "anothertest@example.com");
		Element body = root.addElement("body");
		body.addText("This is a test message");
		byte[] bytes = (root.asXML() + root.asXML()).getBytes(CHARSET);
		this.protocolTranslator.pushDataUp(bytes);
		verify(this.upper, times(2)).pushDataUp(this.upperCaptor.capture());
		this.upperCaptor.getAllValues().forEach((document -> {
			assertEquals(root.asXML(), document.getRootElement().asXML());
		}));
	}
}
