package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StreamFooter;
import org.dom4j.Document;

/**
 * This class translates a StreamFooter into a {@link Document}.
 */
public final class StreamFooterSerializer implements StreamFooterSerializerInterface {

	private static StreamFooterSerializer instance;

	private StreamFooterSerializer() {
	}

	/**
	 * @return this Singleton's instance.
	 */
	public static StreamFooterSerializer getInstance() {
		return (instance != null) ? instance : (instance = new StreamFooterSerializer());
	}

	@Override
	public Document serialize(DataElementInterface element) {
		return StreamFooter.getSerialization();
	}
}
