package edu.kit.ss17.chatsys.team1.shared.Util;

import edu.kit.ss17.chatsys.team1.shared.StreamData.StanzaInterface;

/**
 * Generates unique IDs for each stanza.
 */
public interface IDGeneratorInterface {

	String getID(StanzaInterface stanza);
}
