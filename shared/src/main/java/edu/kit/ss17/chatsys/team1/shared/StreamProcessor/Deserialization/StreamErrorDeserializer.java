package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StreamErrorElement;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.ErrorStanzaErrorException;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jetbrains.annotations.Nullable;

/**
 * This class deserializes stream errors.
 */
public class StreamErrorDeserializer implements DeserializerInterface {

	private static final String STREAM_ERROR = "stream-error", TEXT = "text";

	@Nullable
	@Override
	public DataElementInterface deserializeXML(Branch branch) throws ErrorStanzaErrorException {
		StreamErrorElement streamErrorElement = null;
		Element root = ((Document) branch).getRootElement();
		if (root == null) {
			throw new ErrorStanzaErrorException();
		}
		if (root.getName().equals(STREAM_ERROR)) {
			if (root.elements().size() != 2) {
				throw new ErrorStanzaErrorException();
			}
			StreamErrorCondition streamErrorCondition;
			try {
				streamErrorCondition = StreamErrorCondition.parseValue(((Element) root.elements().get(0)).getName());
			} catch (IllegalArgumentException e) {
				throw new ErrorStanzaErrorException();
			}

			Element text = (Element) root.elements().get(1);

			if (!text.getName().equals(TEXT)) {
				throw new ErrorStanzaErrorException();
			}
			String message = text.getText();

			streamErrorElement = new StreamErrorElement(streamErrorCondition, message);
		}

		return streamErrorElement;
	}
}
