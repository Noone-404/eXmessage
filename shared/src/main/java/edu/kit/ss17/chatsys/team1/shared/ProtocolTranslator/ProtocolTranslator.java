package edu.kit.ss17.chatsys.team1.shared.ProtocolTranslator;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors.StreamProtocolError;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainDataProcessor;
import edu.kit.ss17.chatsys.team1.shared.Util.ClosingTagElement;
import edu.kit.ss17.chatsys.team1.shared.Util.OpeningTagElement;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to translate XML-representation into bytes and vice versa
 */
public class ProtocolTranslator extends ChainDataProcessor<byte[], Document> implements ProtocolTranslatorInterface {

	private static final char    XML_TAG_ENDING      = '>';
	private static final String  ENCODING_STRING     = "UTF-8";
	private static final Charset CHARSET             = Charset.forName(ENCODING_STRING);
	private static final String  STREAM_ELEMENT_NAME = "stream";
	private static final String  CLOSING_STREAM_TAG  = "</stream>";
	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	private final Collection<ProtocolErrorObserverInterface> errorObservers;
	private final StringBuffer                               buffer;
	private final XMLWriter                                  xmlWriter;
	private StringWriter                               stringWriter;
	private final SAXReader                                  reader;

	/**
	 * Default constructor to create a new ProtocolTranslator To be fully functional the upper and lower still need to be set by calling setUpper and setLower
	 */
	public ProtocolTranslator() {
		this.errorObservers = new LinkedList<>();
		this.buffer = new StringBuffer();
		this.stringWriter = new StringWriter();
		OutputFormat outputFormat = new OutputFormat();
		outputFormat.setSuppressDeclaration(true);
		outputFormat.setEncoding(ENCODING_STRING);
		this.xmlWriter = new XMLWriter(this.stringWriter, outputFormat);
		this.reader = new SAXReader();
	}

	@Override
	public void pushDataUp(byte[] data) {
		Collection<Document> documents = new ArrayList<>();
		Document document = processDataFromLower(data);
		while (document != null) {
			documents.add(document);
			//process an empty byte array to see if the current buffer contains another complete Document
			document = processDataFromLower(EMPTY_BYTE_ARRAY);
		}
		documents.forEach((currentDocument) -> {
			this.getUpper().pushDataUp(currentDocument);
		});
	}

	/**
	 * processes a XML-Document and returns its XML-representation as Byte-Array
	 *
	 * @param data The Document to process
	 *
	 * @return XML-representation as Byte-Array
	 */
	@Override
	protected byte[] processDataFromUpper(Document data) {
		byte[] bytes;
		//Stream header and footer need their XML-representation of an opening or closing stream Tag translated into Bytes
		if (STREAM_ELEMENT_NAME.equals(data.getRootElement().getName())) {
			bytes = data.getRootElement().asXML().getBytes(CHARSET);
		}
		//All other XML-Data gets parsed by the XMLWriter and then translated into Bytes
		else {
			try {
				this.xmlWriter.write(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			bytes = this.stringWriter.toString().getBytes(CHARSET);
			this.stringWriter = new StringWriter();
			this.xmlWriter.setWriter(this.stringWriter);
		}
		return bytes;
	}

	/**
	 * Processes incoming Bytes received through the network and returns a Document containing the received XML-data if a complete XML-Element was received, otherwise buffers the
	 * data and waits for the rest
	 *
	 * @param data The data to process
	 *
	 * @return Document containing the received XML-data
	 */
	@Nullable
	@Override
	protected Document processDataFromLower(byte[] data) {
		this.buffer.append(new String(data, CHARSET));
		String currentString = this.buffer.toString();
		currentString = currentString.substring(0, currentString.lastIndexOf(XML_TAG_ENDING) + 1);
		//Try parsing a part of the received Data while there is at least one Tag ending present
		while (currentString.contains(">")) {
			Document document;
			//If the received Data is a closing Stream Tag create the appropriate representation
			if (currentString.trim().equals(CLOSING_STREAM_TAG)) {
				ClosingTagElement closingTagElement = new ClosingTagElement(STREAM_ELEMENT_NAME);
				document = DocumentHelper.createDocument(closingTagElement);
			}
			//If an opening stream Tag is present at the start of the received Data create the appropriate representation
			else if (currentString.trim().startsWith("<stream ") && currentString.contains(">")) {
				currentString = currentString.split(">")[0] + XML_TAG_ENDING;
				String xmlString = currentString + CLOSING_STREAM_TAG;
				try {
					document = this.reader.read(new StringReader(xmlString));
				} catch (DocumentException ignored) {
					ProtocolErrorInterface error = new StreamProtocolError(StreamErrorCondition.BAD_FORMAT, "Malformed opening stream Tag received");
					//TODO change or not ?!
					notifyErrorObservers(error);
					return null;
				}
				Element           rootElement       = document.getRootElement();
				OpeningTagElement openingTagElement = new OpeningTagElement(rootElement.getName());
				openingTagElement.setNamespace(rootElement.getNamespace());
				List<Attribute> attributes = rootElement.attributes();
				for (final Attribute attribute : attributes) {
					openingTagElement.addAttribute(attribute.getName(), attribute.getValue());
				}
				document.setRootElement(openingTagElement);
			}
			//If the data is no opening or closing Stream Tag try parsing a complete XML-Element
			else {
				try {
					document = this.reader.read(new StringReader(currentString));
				}
				//If the received Data is not one complete XML-Element remove the last Tag and jump to the start of the loop
				catch (DocumentException ignored) {
					currentString = currentString.substring(0, currentString.length() - 1);
					currentString = currentString.substring(0, currentString.lastIndexOf(XML_TAG_ENDING) + 1);
					continue;
				}
			}
			this.buffer.delete(0, currentString.length());
			return document;
		}
		//If no part of the received data could be parsed return null and wait for more data to complete an Element
		return null;
	}

	@Override
	public void registerErrorObserver(ProtocolErrorObserverInterface observer) {
		this.errorObservers.add(observer);
	}

	@Override
	public void unregisterErrorObserver(ProtocolErrorObserverInterface observer) {
		this.errorObservers.remove(observer);
	}

	/**
	 * notifies the registered ErrorObserver when a protocol error has occurred
	 *
	 * @param error an error element containing information about the error that occurred
	 */
	private void notifyErrorObservers(ProtocolErrorInterface error) {
		for (final ProtocolErrorObserverInterface observer : this.errorObservers) {
			observer.onProtocolError(error);
		}
	}
}
