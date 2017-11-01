package edu.kit.ss17.chatsys.team1.shared.StreamData;

import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Serialization.IQSerializerInterface;
import org.dom4j.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * IQ to represent client server interaction.
 */
public class IQ extends AbstractStanza implements IQInterface {

	private static IQSerializerInterface iQSerializer;

	private String request;
	private Collection<String> parameterNames  = new LinkedList<>();
	private Collection<String> parameterValues = new LinkedList<>();
	private Collection<String> defaultContent = new ArrayList<>();
	private DataElementInterface content;

	/**
	 * @param iQSerializer the serializer that is responsible for translating this object to a {@link Document}.
	 */
	public static void setIQSerializer(IQSerializerInterface iQSerializer) {
		IQ.iQSerializer = iQSerializer;
	}

	/**
	 * @return the serializer that is responsible for translating this object to a {@link Document}.
	 */
	public static IQSerializerInterface getIQSerializer() {
		return iQSerializer;
	}

	@Override
	public Document serialize() {
		return iQSerializer.serialize(this);
	}

	@Override
	public String getRequest() {
		return request;
	}

	@Override
	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	public Collection<String> getParameterNames() {
		return parameterNames;
	}

	@Override
	public void addParameterName(String parameterName) {
		this.parameterNames.add(parameterName);
	}

	@Override
	public void clearParameterNames() {
		this.parameterNames.clear();
	}

	@Override
	public Collection<String> getParameterValues() {
		return parameterValues;
	}

	@Override
	public void addParameterValue(String parameterValue) {
		this.parameterValues.add(parameterValue);
	}

	@Override
	public void clearParameterValues() {
		this.parameterValues.clear();
	}

	@Override
	public DataElementInterface getContent() {
		return content;
	}

	@Override
	public void setContent(DataElementInterface content) {
		this.content = content;
	}

	@Override
	public Collection<String> getDefaultContent() {
		return defaultContent;
	}

	@Override
	public void addDefaultContent(String contentString) {
		defaultContent.add(contentString);
	}

	@Override
	public void clearDefaultContent() {
		defaultContent.clear();
	}

	@Override
	public String toString() {
		return "IQ{" +
		       "request='" + request + '\'' +
		       ", parameterNames=" + parameterNames +
		       ", parameterValues=" + parameterValues +
		       ", defaultContent=" + defaultContent +
		       ", content=" + content +
		       "} " + super.toString();
	}
}
