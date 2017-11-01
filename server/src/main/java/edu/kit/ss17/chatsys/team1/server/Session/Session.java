package edu.kit.ss17.chatsys.team1.server.Session;

import edu.kit.ss17.chatsys.team1.server.Controller.ConnectionStackInterface;
import edu.kit.ss17.chatsys.team1.shared.ConnectionStack.ConnectionStackBaseInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureSet;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureSetInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.ErrorElement;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors.StreamProtocolError;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.*;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainUpperDataProcessor;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;

import java.util.*;

/**
 * The Session class represents the state of a single session.
 */
public class Session extends ChainUpperDataProcessor<DataElementInterface> implements SessionInterface {

	private static final int AUTHENTICATION_PRIORITY_CLASS = 10;
	private static final int FIRST_PRIORITY_CLASS          = 1;
	private static final int LAST_PRIORITY_CLASS           = 20;
	private static final int LAST_REQUIRED_PRIORITY_CLASS  = 19;
	private Map<Integer, Collection<NegotiatorInterface>> negotiators;
	private JID                                           jid;
	private PresenceValueInterface                        presence;
	private Collection<ProtocolErrorObserverInterface>    errorObservers;
	private int                                           currentPriority;
	private boolean                                       isDisconnecting;
	private boolean                                       finished;
	private Collection<NegotiatorInterface>               currentNegotiators;
	private ConnectionStackInterface                      connectionStack;


	/**
	 * Constructor to initialize the Session The Session still needs a lower (set by calling setLower()) to be fully functional
	 *
	 * @param storage Storage Object that allows access to the accounts of this server
	 */
	public Session(StorageInterface storage) {
		this.currentPriority = 1;
		this.negotiators = new HashMap<>();
		this.errorObservers = new LinkedList<>();
		Authenticator authenticator = new Authenticator(storage);
		addNegotiator(authenticator);
	}

	/**
	 * Adds a negotiator in the appropriate priority class
	 *
	 * @param negotiator negotiator to be added
	 */
	private void addNegotiator(NegotiatorInterface negotiator) {
		int priority;
		if (negotiator instanceof AuthenticatorInterface) {
			// Authentication always in the same priority class
			priority = AUTHENTICATION_PRIORITY_CLASS;
		} else if (negotiator.getWeight() <= FIRST_PRIORITY_CLASS) {
			// Put negotiators with the lowest possible priority class
			priority = FIRST_PRIORITY_CLASS;
		} else if (negotiator.getWeight() >= LAST_PRIORITY_CLASS) {
			//The last priority class for required features
			if (negotiator.isRequired()) {
				priority = LAST_REQUIRED_PRIORITY_CLASS;
			} else {
				// The last priority class for optional features
				priority = LAST_PRIORITY_CLASS;
			}
		} else {
			priority = negotiator.getWeight();
		}

		Collection<NegotiatorInterface> currentPriorityNegotiators = this.negotiators.get(priority);

		if (currentPriorityNegotiators != null) {
			currentPriorityNegotiators.add(negotiator);
		} else {
			Collection<NegotiatorInterface> priorityNegotiators = new HashSet<>();
			priorityNegotiators.add(negotiator);
			this.negotiators.put(priority, priorityNegotiators);
		}
	}


	@Override
	public void tryRegisterPlugin(PluginInterface plugin, boolean lastPlugin) {
		if (plugin instanceof SessionPluginInterface) {
			SessionPluginInterface sessionPlugin = (SessionPluginInterface) plugin;
			addNegotiator(sessionPlugin);
		}
	}

	@Override
	public JID getJid() {
		return this.jid;
	}

	@Override
	public void setConnectionStack(ConnectionStackBaseInterface connectionStack) {
		this.connectionStack = (ConnectionStackInterface) connectionStack;
	}

	/**
	 * Method to check whether the current negotiation step is finished
	 */
	private boolean isCurrentStepFinished() {
		if (this.currentNegotiators == null) {
			return true;
		} else {
			boolean hasRequiredFeature = false;
			for (NegotiatorInterface currentNegotiator : this.currentNegotiators) {
				if (currentNegotiator.isRequired()) {
					hasRequiredFeature = true;
					if (currentNegotiator.isNegotiationFinished() && !currentNegotiator.hasNegotiationFailed()) {
						if (currentNegotiator instanceof AuthenticatorInterface) {
							AuthenticatorInterface authenticator = (AuthenticatorInterface) currentNegotiator;
							this.jid = authenticator.getJid();
						}
						return true;
					}
				}
			}
			return !hasRequiredFeature;
		}
	}

	/**
	 * Method to initiate the next negotiation step Collects the negotiation features for the next step an sends them to the client and checks whether there are still required
	 * features to negotiate
	 */
	private void startNextNegotiationStep() {
		this.currentNegotiators = new LinkedList<>();
		boolean hasRequiredFeature = false;
		while (!hasRequiredFeature && this.currentPriority < LAST_PRIORITY_CLASS) {
			++this.currentPriority;
			Collection<NegotiatorInterface> nextPriorityNegotiators = this.negotiators.get(this.currentPriority);
			if (nextPriorityNegotiators != null) {
				for (NegotiatorInterface currenNegotiator : nextPriorityNegotiators) {
					if (currenNegotiator.isRequired()) {
						hasRequiredFeature = true;
					}
				}
				this.currentNegotiators.addAll(nextPriorityNegotiators);
			}
		}
		if (!hasRequiredFeature) {
			//If there are no more required features to negotiate, negotiation is finished
			this.finished = true;
			this.connectionStack.negotiationFinished(true);
		}
	}

	/**
	 * Initiates the session or the next negotiation step when a stream header was sent by the client
	 *
	 * @param header stream header sent by the client
	 */
	private void initSession(StreamHeaderInterface header) {
		//Send a response header to the client
		StreamHeaderInterface responseHeader = new StreamHeader(UUID.randomUUID().toString(), header.getTo(), header.getFrom(), "jabber:server");
		this.getLower().pushDataDown(responseHeader);
		//Check whether the current negotiation step is finished and initiate the next step if it is
		if (isCurrentStepFinished()) {
			startNextNegotiationStep();
			JID requestedJID = null;
			try {
				requestedJID = new JID(header.getFrom());
			} catch (InvalidJIDException ignored) {
				ProtocolErrorInterface error = new StreamProtocolError(StreamErrorCondition.INVALID_FROM, "From attribute in the stream header is not a valid JID");
				notifyObservers(error);
				return;
			}
			//If authentication was negotiated already stream header needs to fit the authenticated JID
			if (this.currentPriority > AUTHENTICATION_PRIORITY_CLASS) {
				if (!this.jid.getLocalPart().equals(requestedJID.getLocalPart()) || !this.jid.getDomainPart().equals(requestedJID.getDomainPart())) {
					ProtocolErrorInterface error = new StreamProtocolError(StreamErrorCondition.INVALID_FROM, "Stream header doesn't fit authenticated JID");
					notifyObservers(error);
					return;
				}
			}
			//Set JID to the value from the stream header if the JID is not yet authenticated or requested JID fits the authenticated JID
			this.jid = requestedJID;
			//Collect Features for the next negotiation step an send them to the client
			Collection<NegotiationFeatureInterface> features = new LinkedList<>();
			for (NegotiatorInterface currentNegotiator : this.currentNegotiators) {
				features.add(currentNegotiator.getNegotiationFeature());
			}
			NegotiationFeatureSetInterface featureSet = new NegotiationFeatureSet(features);
			this.getLower().pushDataDown(featureSet);
		}
		//If the current step is not finished an Error has occurred
		else {
			StreamProtocolError error = new StreamProtocolError(StreamErrorCondition.POLICY, "Opening a new stream is not allowed in the current negotiation state");
			notifyObservers(error);
		}
	}

	/**
	 * Method to notify the registered Error observers when an error has occurred
	 *
	 * @param error representation of the error that occurred
	 */
	private void notifyObservers(ProtocolErrorInterface error) {
		for (ProtocolErrorObserverInterface observer : this.errorObservers) {
			observer.onProtocolError(error);
		}
	}

	/**
	 * Checks if a given session is negotiated successfully for the given JID and ready to send and receive stanzas.
	 *
	 * @return Indicates whether the session is completely negotiated for the given JID
	 */
	@Override
	public boolean isNegotiationFinished() {
		return this.finished;
	}

	@Override
	public void terminateSession() {
		if (!isDisconnecting()) {
			// We didn't send the closing tag yet.
			DataElementInterface streamFooter = new StreamFooter();
			this.setDisconnecting(true);
			this.getLower().pushDataDown(streamFooter);
		}
	}

	/**
	 * Method to process negotiation data sent by the client lets the negotiators process the data and sends the response to the client
	 *
	 * @param data negotiation data sent by the client
	 */
	private void negotiateSession(DataElementInterface data) {
		if (this.currentNegotiators != null) {
			for (NegotiatorInterface currentNegotiator : this.currentNegotiators) {
				if (!currentNegotiator.isNegotiationFinished()) {
					DataElementInterface response = currentNegotiator.negotiate(data);
					if (response != null) {
						this.getLower().pushDataDown(response);
						return;
					}
					if (currentNegotiator.isNegotiationFinished()) {
						return;
					}
				}
			}
			//If no negotiator responds or changes it's state to finished, the negotiation data was invalid
			ProtocolErrorInterface error = new StreamProtocolError(StreamErrorCondition.UNDEFINED, "Negotiation Data could not be processed");
			notifyObservers(error);
		} else {
			ProtocolErrorInterface error = new StreamProtocolError(StreamErrorCondition.UNDEFINED, "Negotiation not allowed in the current state");
			notifyObservers(error);
		}
	}

	@Override
	public PresenceValueInterface getPresence() {
		return this.presence;
	}

	@Override
	public void setPresence(PresenceValueInterface presence) {
		this.presence = presence;
	}

	@Override
	public void setDisconnecting(boolean disconnecting) {
		this.isDisconnecting = disconnecting;
	}

	@Override
	public boolean isDisconnecting() {
		return this.isDisconnecting;
	}

	@Override
	public void registerErrorObserver(ProtocolErrorObserverInterface observer) {
		this.errorObservers.add(observer);
	}

	@Override
	public void unregisterErrorObserver(ProtocolErrorObserverInterface observer) {
		this.errorObservers.remove(observer);
	}

	/**
	 * Called by StreamProcessor if negotiation is still running and there is data to be negotiated.
	 *
	 * @param data negotiation data sent by the client
	 */
	@Override
	public void pushDataUp(DataElementInterface data) {
		if (data instanceof StreamHeaderInterface) {
			StreamHeaderInterface header = (StreamHeaderInterface) data;
			initSession(header);
		}
		else if (data instanceof ErrorElement || data instanceof StreamFooterInterface) {
			this.connectionStack.pushDataUp(data);
		}
		else {
			negotiateSession(data);
		}
	}
}
