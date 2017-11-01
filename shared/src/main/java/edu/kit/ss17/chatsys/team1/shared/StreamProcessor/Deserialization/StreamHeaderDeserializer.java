package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StreamHeader;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StreamHeaderInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StreamErrorException;
import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * This class deserializes stream headers.
 */
public class StreamHeaderDeserializer implements DeserializerInterface {

	private static final String STREAM = "stream", FROM = "from", STREAM_ID = "id", TO = "to";
	private static final String ERROR_MESSAGE = "Error, a stream header should containt at least the " +
	                                            "three attributes 'from', 'id' and 'to'.";

	@Nullable
	@Override
	public DataElementInterface deserializeXML(Branch branch) throws StreamErrorException {
		StreamHeaderInterface streamHeader = null;
		Element               root         = ((Document) branch).getRootElement();
		if (root.getName().equals(STREAM)) {
			String from = null, id = null, to = null, namespace;
			if (root.attributeCount() < 3) {
				throw new StreamErrorException(StreamErrorCondition.BAD_FORMAT, ERROR_MESSAGE);
			}
			for (final Attribute attribute : (Collection<Attribute>) root.attributes()) {
				if (attribute.getName().equals(FROM)) {
					from = attribute.getValue();
				} else if (attribute.getName().equals(STREAM_ID)) {
					id = attribute.getValue();
				} else if (attribute.getName().equals(TO)) {
					to = attribute.getValue();
				}
			}
			if (from == null) {
				throw new StreamErrorException(StreamErrorCondition.BAD_FORMAT, "Missing attribute 'from' in stream header.");
			}
			if (id == null) {
				throw new StreamErrorException(StreamErrorCondition.BAD_FORMAT, "Missing attribute 'id' in stream header.");
			}
			if (to == null) {
				throw new StreamErrorException(StreamErrorCondition.BAD_FORMAT, "Missing attribute 'to' in stream header.");
			}
			namespace = root.getNamespaceURI();
			streamHeader = new StreamHeader(id, from, to, namespace);
		}
		return streamHeader;
	}
}
