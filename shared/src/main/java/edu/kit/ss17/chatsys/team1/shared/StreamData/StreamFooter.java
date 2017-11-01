package edu.kit.ss17.chatsys.team1.shared.StreamData;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.StreamFooterSerializerInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ClosingTagElement;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.jetbrains.annotations.Contract;

/**
 * Class to represent the Stream footer (aka closing Stream Tag)
 */
public class StreamFooter implements StreamFooterInterface {

	private static final String   STREAM        = "stream";
	private static final Document serialization = DocumentHelper.createDocument(new ClosingTagElement(STREAM));
	private static StreamFooterSerializerInterface streamFooterSerializer;

	/**
	 * @param streamFooterSerializer the serializer that is responsible for translating this object to a {@link Document}.
	 */
	public static void setStreamFooterSerializer(StreamFooterSerializerInterface streamFooterSerializer) {
		StreamFooter.streamFooterSerializer = streamFooterSerializer;
	}

	@Override
	public Document serialize() {
		return streamFooterSerializer.serialize(this);
	}

	/**
	 * @return the {@link Document} representation of a closing stream tag.
	 */
	@Contract(pure = true)
	public static Document getSerialization() {
		return serialization;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof StreamFooter;
	}

	@Override
	public int hashCode() {
		final int prime  = 31;
		int       result = 1;
		result = result * prime;
		return result;
	}

	@Override
	public String toString() {
		return "StreamFooter{}";
	}
}
