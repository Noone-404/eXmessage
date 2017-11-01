package edu.kit.ss17.chatsys.team1.shared.StreamData;

import org.dom4j.Document;

/**
 * This Interface is the root interface for all Interfaces and their implementing classes whose XML-representation will get sent through the stream.
 */
public interface DataElementInterface {

	/**
	 * Every DataElementInterface object provides a method to serialize itself to a Document. An implementation should pass on a serialize request to a corresponding
	 * DataElementSerializerInterface implementation.
	 *
	 * @return Document as serialization of itself.
	 */
	Document serialize();
}
