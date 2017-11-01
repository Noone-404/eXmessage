package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import org.dom4j.Document;

/**
 * This is the root interface for all serializers.
 * An implementing class must translate a {@link DataElementInterface} into a {@link Document}.
 */
public interface DataElementSerializerInterface {

	/**
	 * This method serializes a DataElementInterface object to a Document.
	 *
	 * @param element The DataElementInterface object to be serialized.
	 *
	 * @return The serialization as Document.
	 */
	Document serialize(DataElementInterface element);
}
