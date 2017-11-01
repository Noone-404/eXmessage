package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StanzaErrorType;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.IQ;
import edu.kit.ss17.chatsys.team1.shared.StreamData.IQInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Constants;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StreamProcessorException;
import org.dom4j.Attribute;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This class deserializes IQ objects.
 */
public class IQDeserializer extends AbstractStanzaDeserializer {

	private static final String                            IQ                   = "iq";
	private              Collection<IQContentDeserializerInterface> contentDeserializers = new LinkedList<>();

	@Nullable
	@Override
	public DataElementInterface deserializeXML(Branch branch) throws StanzaErrorException {
		IQInterface iq   = new IQ();
		Element     root = ((Document) branch).getRootElement();
		if (root == null) {
			throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.ERROR, "", "", "",
			                               Constants.EMPTY_XML_MESSAGE);
		}
		if (root.getName().equals(IQ)) {
			iq = (IQInterface) deserializeMetadata(root, iq);
			if (iq != null) { // if iq == null then deserialize decided it's not a stanza
				if (root.elements().size() == 1) {
					Element child = (Element) root.elements().get(0);
					iq.setRequest(child.getName());
					for (final Attribute attribute : (List<Attribute>) child.attributes()) {
						iq.addParameterName(attribute.getName());
						iq.addParameterValue(attribute.getValue());
					}

					if (child.elements().size() > 1) {
						throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.IQ, iq.getSender(),
						                               iq.getReceiver(), iq.getID(), "An IQ stanza should have exactly one grand child.");
					} else if (child.elements().size() == 1) {
						Element content = (Element) child.elements().get(0);
						for (String defaultContent : content.getText().split("\n")) {
							if (!defaultContent.isEmpty()) {
								iq.addDefaultContent(defaultContent);
							}
						}
						iq.setContent(deserializeContent(content));
					}
				} else {
					throw new StanzaErrorException(StanzaErrorCondition.BAD_REQUEST, StanzaErrorType.MODIFY, Constants.IQ, iq.getSender(),
					                               iq.getReceiver(), iq.getID(), "An IQ stanza should have exactly one child.");
				}
			}
		} else {
			iq = null;
		}

		return iq;
	}

	/**
	 * @param deserializer {@link IQContentDeserializerInterface} to be added to the Collection.
	 */
	public void addDeserializer(IQContentDeserializerInterface deserializer) {
		this.contentDeserializers.add(deserializer);
	}

	/**
	 * Clears the Collection of {@link IQContentDeserializerInterface}.
	 */
	public void clearDeserializers() {
		this.contentDeserializers = new LinkedList<>();
	}

	private DataElementInterface deserializeContent(Branch branch) throws StanzaErrorException {
		DataElementInterface dataElement = null;
		try {
			for (final DeserializerInterface deserializer : this.contentDeserializers) {
				dataElement = deserializer.deserializeXML(branch);
				if (dataElement != null) {
					break;
				}
			}
		} catch (StanzaErrorException e) {
			throw e;
		} catch (StreamProcessorException ignore) { // this should not happen unless a plugin breaks in which case we don't care
		}
		return dataElement;
	}
}
