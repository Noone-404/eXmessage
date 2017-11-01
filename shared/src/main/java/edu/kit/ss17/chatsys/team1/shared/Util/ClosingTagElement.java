package edu.kit.ss17.chatsys.team1.shared.Util;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;

import java.io.IOException;
import java.io.StringWriter;

/**
 *
 */
public class ClosingTagElement extends DefaultElement {

	private static final String ENCODING_STRING = "UTF-8";
	private StringWriter stringWriter;

	public ClosingTagElement(String name) {
		super(name);
		this.stringWriter = new StringWriter();
	}

	@Override
	public String asXML() {
		OutputFormat outputFormat = new OutputFormat();
		outputFormat.setEncoding(ENCODING_STRING);
		outputFormat.setExpandEmptyElements(true);
		XMLWriter xmlWriter;
		xmlWriter = new XMLWriter(this.stringWriter, outputFormat);
		try {
			xmlWriter.write(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String xmlString = this.stringWriter.toString();
		this.stringWriter.flush();
		String[] xmlParts = xmlString.split("<");
		return "<" + xmlParts[xmlParts.length - 1];

	}
}
