package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorType;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.StanzaErrorElement;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Constants;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.ErrorStanzaErrorException;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * This class deserializes stanza errors.
 */
public class StanzaErrorDeserializer implements DeserializerInterface {

	private static final String ID = "id", FROM = "from", TO = "to", TYPE = "type", TEXT = "text";

	@Nullable
	@Override
	public DataElementInterface deserializeXML(Branch branch) throws ErrorStanzaErrorException {
		StanzaErrorElement stanzaError = null;
		Element root = ((Document) branch).getRootElement();
		if (root == null) {
			throw new ErrorStanzaErrorException();
		}

		String stanzaType = root.getName(), type = null, from = null, id = null, to = null;
		if (root.attributeCount() < 4) {
			return null;
		}
		for (final Attribute attribute : (Collection<Attribute>) root.attributes()) {
			if (attribute.getName().equals(FROM)) {
				from = attribute.getValue();
			} else if (attribute.getName().equals(ID)) {
				id = attribute.getValue();
			} else if (attribute.getName().equals(TO)) {
				to = attribute.getValue();
			} else if (attribute.getName().equals(TYPE)) {
				type = attribute.getValue();
			}
		}
		if (!type.equals(Constants.ERROR)) {
			return null;
		}
		if (from == null) {
			throw new ErrorStanzaErrorException();
		}
		if (id == null) {
			throw new ErrorStanzaErrorException();
		}
		if (to == null) {
			throw new ErrorStanzaErrorException();
		}

		if (root.elements().size() != 1) {
			throw new ErrorStanzaErrorException();
		}
		Element child = (Element) root.elements().get(0);
		if (!(child.getName().equals(Constants.ERROR))) {
			throw new ErrorStanzaErrorException();
		}
		if (child.attributeCount() != 1) {
			throw new ErrorStanzaErrorException();
		}
		if (!child.attribute(0).getName().equals(TYPE)) {
			throw new ErrorStanzaErrorException();
		}
		StanzaErrorType errorType;
		try {
			errorType = StanzaErrorType.parseValue(child.attribute(0).getValue());
		} catch (IllegalArgumentException ignore) {
			throw new ErrorStanzaErrorException();
		}

		if (child.elements().size() != 2) {
			throw new ErrorStanzaErrorException();
		}
		StanzaErrorCondition errorCondition;
		try {
			errorCondition = StanzaErrorCondition.parseValue(((Element) child.elements().get(0)).getName());
		} catch (IllegalArgumentException ignore) {
			throw new ErrorStanzaErrorException();
		}

		Element text = (Element) child.elements().get(1);
		if (!text.getName().equals(TEXT)) {
			throw new ErrorStanzaErrorException();
		}
		String message = text.getText();

		stanzaError = new StanzaErrorElement(from, to, stanzaType, id, errorType, errorCondition, message, false);

		return stanzaError;
	}
}
