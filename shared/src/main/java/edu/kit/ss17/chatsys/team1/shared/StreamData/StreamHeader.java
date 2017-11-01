package edu.kit.ss17.chatsys.team1.shared.StreamData;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.StreamHeaderSerializerInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import org.dom4j.Document;

import java.util.UUID;

/**
 * Class to represent the stream header sent to open a new XML-stream
 */
public class StreamHeader implements StreamHeaderInterface {

	private static StreamHeaderSerializerInterface streamHeaderSerializer;

	private String streamID;
	private String from;
	private String to;
	private String namespace;

	/**
	 * constructor to create a Stream header based on JID this will create the header for a client session with namespace "jabber:client" and a random StreamID
	 */
	public StreamHeader(JID jid) {
		this.namespace = "jabber:client";
		this.from = jid.getFullJID();
		this.to = jid.getDomainPart();
		this.streamID = UUID.randomUUID().toString();
	}

	/**
	 * constructor to create a Stream header with the given attributes
	 */
	public StreamHeader(String streamID, String from, String to, String namespace) {
		this.streamID = streamID;
		this.from = from;
		this.to = to;
		this.namespace = namespace;
	}

	/**
	 * @param streamHeaderSerializer the serializer that is responsible for translating this object to a {@link Document}.
	 */
	public static void setSerializer(StreamHeaderSerializerInterface streamHeaderSerializer) {
		StreamHeader.streamHeaderSerializer = streamHeaderSerializer;
	}

	/**
	 * @return StreamHeaderSerializerInterface the serializer that is responsible for translating this object to a {@link Document}.
	 */
	public static StreamHeaderSerializerInterface getSerializer() {
		return streamHeaderSerializer;
	}

	@Override
	public String getStreamID() {
		return this.streamID;
	}

	@Override
	public String getFrom() {
		return this.from;
	}

	@Override
	public String getTo() {
		return this.to;
	}

	@Override
	public String getNamespace() {
		return this.namespace;
	}

	@Override
	public Document serialize() {
		return streamHeaderSerializer.serialize(this);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof StreamHeader) {
			StreamHeader streamHeader = (StreamHeader) o;
			return this.streamID.equals(streamHeader.getStreamID()) && this.to.equals(streamHeader.getTo())
			       && this.from.equals(streamHeader.getFrom()) && this.namespace.equals(streamHeader.getNamespace());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime  = 31;
		int       result = 1;
		result = result * prime + this.namespace.hashCode();
		result = result * prime + this.from.hashCode();
		result = result * prime + this.to.hashCode();
		result = result * prime + this.streamID.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "StreamHeader{" +
		       "streamID='" + streamID + '\'' +
		       ", from='" + from + '\'' +
		       ", to='" + to + '\'' +
		       ", namespace='" + namespace + '\'' +
		       '}';
	}
}
