package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.dom4j.Document;

/**
 * This interface declares the functionality of all DeserializationManagers.
 *
 * A DeserializationManager offers the functionality to translate XML-Documents to DataElementInterfaces.
 * It uses an internal Collection of {@link DeserializerInterface}s and lets those do the translation.
 */
public interface DeserializationManagerInterface {

	/**
	 * This method deserializes a {@link Document} and returns the generated {@link DataElementInterface} object.
	 *
	 * @param document {@link Document} that gets deserialized to a {@link DataElementInterface} object.
	 *
	 * @return the generated {@link DataElementInterface} object.
	 */
	DataElementInterface deserializeXML(Document document);

	/**
	 * This method adds a {@link DeserializerInterface} to the Collection of {@link DeserializerInterface} that gets used to deserialize XML-Documents.
	 *
	 * @param deserializerInterface A DeserializerInterface that should be used in the DeserializationManager
	 */
	void addDeserializerInterface(DeserializerInterface deserializerInterface);

	/**
	 * This method empties the Collection of Deserializers that get used to deserialize XML-Documents.
	 */
	void clearDeserializerInterfaceCollection();
}
