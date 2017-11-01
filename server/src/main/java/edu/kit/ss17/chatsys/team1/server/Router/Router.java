package edu.kit.ss17.chatsys.team1.server.Router;

import edu.kit.ss17.chatsys.team1.server.Controller.ConnectionStackInterface;
import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.IQ;
import edu.kit.ss17.chatsys.team1.shared.StreamData.IQInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.StanzaInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Standard implementation of @code{RouterInterface}.
 */
public class Router implements RouterInterface {

	private static Router instance;

	private HashMap<String, ConnectionStackInterface> map = new HashMap<>();
	private StorageInterface storage;

	private Router() {
	}

	public static RouterInterface getInstance() {
		return (instance != null) ? instance : (instance = new Router());
	}

	@Override
	public void registerConnectionStack(ConnectionStackInterface stack) {
		if (stack != null && stack.getJID() != null) {
			this.map.put(stack.getJID().getBaseJID(), stack);
		}
	}

	@Override
	public void unregisterConnectionStack(ConnectionStackInterface stack) {
		if (stack != null && stack.getJID() != null) { // just in case
			this.map.remove(stack.getJID().getBaseJID());
		}
	}

	@Override
	public void sendStanza(StanzaInterface stanza) {
		JID jid;
		try {
			jid = new JID(stanza.getReceiver());
		} catch (InvalidJIDException ignored) {
			return; // drop it
		}
		if (this.map.containsKey(jid.getBaseJID())) {
			this.map.get(jid.getBaseJID()).sendStanzaToUser(stanza);
		}
	}

	@Override
	public void processStanza(StanzaInterface stanza) {
		if (!(stanza instanceof IQInterface))
			return; // only IQs can be destined for the server only

		IQInterface iq = (IQInterface) stanza;

		// TODO: das gehört tlw. eigentlich in irgend ne Klasse Richtung StreamProcessor. Andere Komponenten sollten *nie* selbst irgendwelche String-Literale abfragen müssen.
		if (iq.getRequest().equals("jid-search")) {
			// TODO: Die IQ-Klasse ist schrecklich designed. Das sollte angepasst werden, damit sowas wie hier nicht mehr nötig ist.
			Iterator<String> valueIterator = iq.getParameterValues().iterator();
			String           searchString  = null;
			for (String s : iq.getParameterNames()) {
				if (s.equals("jid-fragment")) {
					searchString = valueIterator.next();
					break;
				} else
					valueIterator.next();

				if (!valueIterator.hasNext())
					break;
			}

			if (searchString == null)
				return;

			JID jid;
			try {
				jid = new JID(stanza.getSender());
			} catch (InvalidJIDException ignored) {
				return; // drop it
			}

			if (!this.map.containsKey(jid.getBaseJID()))
				return;

			Collection<String>       responseValues = performSearchRequest(searchString);
			ConnectionStackInterface responseUser   = this.map.get(jid.getBaseJID());

			IQInterface responseStanza = new IQ();
			// TODO: genauso wie oben; das gehört eigentlich in ne StreamProcessor-Klasse ausgelagert.
			responseStanza.setRequest("jid-search");
			responseStanza.setType("result");
			responseStanza.addParameterName("jid-fragment");
			responseStanza.addParameterValue(searchString);
			responseValues.forEach(responseStanza::addDefaultContent);
			responseStanza.setID(iq.getID());
			responseStanza.setServerReceiveDate(Instant.now());
			responseStanza.setReceiver(jid.getFullJID());
			responseUser.sendStanzaToUser(responseStanza);
		}
	}

	@Override
	public void setStorage(StorageInterface storage) {
		this.storage = storage;
	}

	private Collection<String> performSearchRequest(String search) {
		if (search.length() < 3)
			return new ArrayList<>();

		return this.storage.getAllAccountJids().stream().map(JID::getBaseJID).filter(jid -> jid.toLowerCase().contains(search.toLowerCase())).collect(Collectors.toList());
	}
}
