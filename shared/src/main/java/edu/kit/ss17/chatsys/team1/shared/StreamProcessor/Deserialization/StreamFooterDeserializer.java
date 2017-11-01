package edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Deserialization;

import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StreamFooter;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Exceptions.StanzaErrorException;
import org.dom4j.Branch;
import org.jetbrains.annotations.Nullable;

/**
 * This class deserializes stream footers (a.k.a. closing stream tags).
 */
public class StreamFooterDeserializer implements DeserializerInterface {

	@Nullable
	@Override
	public DataElementInterface deserializeXML(Branch branch) throws StanzaErrorException {
		StreamFooter streamFooter = new StreamFooter();
		return branch.asXML().equals(streamFooter.getSerialization().asXML()) ? streamFooter : null;
	}
}
