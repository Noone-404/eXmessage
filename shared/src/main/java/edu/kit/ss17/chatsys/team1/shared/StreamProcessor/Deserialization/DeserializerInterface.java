package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StreamErrorException;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StreamProcessorException;
import org.dom4j.Branch;
import org.jetbrains.annotations.Nullable;

/**
 * This interface declares the functionality of a Deserializer.
 * A Deserializer translates a {@link Branch} into a {@link DataElementInterface}.
 */
public interface DeserializerInterface {

	/**
	 * Translates an XML-Branch to a DataElementInterface. Returns null if it can't translate the Branch-object.
	 *
	 * @param branch the xml document to be deserialized
	 *
	 * @return A DataElementInterface if the deserialization was successful or null if not.
	 */
	@Nullable
	DataElementInterface deserializeXML(Branch branch) throws StreamProcessorException;
}
