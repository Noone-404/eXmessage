package edu.kit.ss17.chatsys.team1.client.Session;

import edu.kit.ss17.chatsys.team1.client.Controller.ConnectionStackInterface;
import edu.kit.ss17.chatsys.team1.shared.ConnectionStack.ConnectionStackBaseInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureSetInterface;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorConstants.StreamErrorCondition;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ErrorElements.ErrorElement;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.Errors.StreamProtocolError;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.*;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.ChainDataProcessor.ChainUpperDataProcessor;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;

import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;


/**
 * Class to represent the current state of the session.
 */
public class Session extends ChainUpperDataProcessor<DataElementInterface> implements SessionInterface {

	private Collection<NegotiatorInterface>            negotiators;
	private boolean                                    isDisconnecting;
	private PresenceValueInterface                     presence;
	private Collection<ProtocolErrorObserverInterface> errorObservers;
	private boolean                                    finished;
	private Collection<NegotiationFeatureInterface>    currentFeatures;
	private NegotiatorInterface                        currentNegotiator;
	private NegotiationFeatureInterface                currentFeature;
	private JID                                        jid;
	private ConnectionStackInterface                   connectionStack;


	/**
	 * Constructor for a new Session The Session still needs a lower (set by calling setLower()) to be functional call initSession() to make the Session start negotiating
	 */
	public Session() {
		this.negotiators = new LinkedList<>();
		this.errorObservers = new LinkedList<>();
	}

	@Override
	public void setConnectionStack(ConnectionStackBaseInterface connectionStack) {
		this.connectionStack = (ConnectionStackInterface) connectionStack;
	}

	@Override
	public boolean isNegotiationFinished() {
		return this.finished;
	}

	@Override
	public void initSession(AccountInterface account) {
		// If the JID has no resource part create a duplicate (so we don't change the original JID in the account Object) and set the resource part to a random value
		if (account.getJid().getResourcePart().isEmpty()) {
			this.jid = new JID();
			this.jid.setLocalPart(account.getJid().getLocalPart());
			this.jid.setDomainPart(account.getJid().getDomainPart());
			this.jid.setResourcePart(UUID.randomUUID().toString());
		} else {
			// Use the JID from the account object if it already has a resource part
			this.jid = account.getJid();
		}
		this.negotiators.add(new Authenticator(account));
		StreamHeaderInterface streamHeader = new StreamHeader(this.jid);
		this.getLower().pushDataDown(streamHeader);
	}

	@Override
	public void setPresence(PresenceValueInterface newValue) {
		this.presence = newValue;
	}

	@Override
	public PresenceValueInterface getPresence() {
		return this.presence;
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
	 * Method to start negotiating a feature from the given Collection of features
	 *
	 * @param features Collection of features to negotiate
	 *
	 * @return indicates whether one of the negotiators has started negotiating a feature
	 */
	private boolean tryNegotiateFeature(Collection<NegotiationFeatureInterface> features) {
		for (final NegotiationFeatureInterface currentFeature : features) {
			for (final NegotiatorInterface currentNegotiator : this.negotiators) {
				if (currentNegotiator.isNegotiationFinished()) {
					continue;
				} else {
					DataElementInterface negotiationData = currentNegotiator.startNegotiation(currentFeature);
					if (negotiationData != null) {
						//If a negotiator responds with negotiation Data remove the feature from the list of features that still need to be negotiated
						this.currentFeatures.remove(currentFeature);
						//Update currently negotiating feature and negotiator
						this.currentFeature = currentFeature;
						this.currentNegotiator = currentNegotiator;
						//Send negotiation data to the server
						this.getLower().pushDataDown(negotiationData);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Method to start negotiating the features sent by the server
	 */
	private void startNegotiation() {
		Collection<NegotiationFeatureInterface> optionalFeatures = new LinkedList<>();
		Collection<NegotiationFeatureInterface> requiredFeatures = new LinkedList<>();
		//Split features by optional and required
		for (final NegotiationFeatureInterface currentFeature : this.currentFeatures) {
			if (currentFeature.isRequired()) {
				requiredFeatures.add(currentFeature);
			} else {
				optionalFeatures.add(currentFeature);
			}
		}
		//Try negotiating optional Features first
		if (tryNegotiateFeature(optionalFeatures)) {
			return;
		}
		//Negotiation is finished if no optional feature can be negotiated and there are no required features
		else if (requiredFeatures.isEmpty()) {
			this.finished = true;
			this.connectionStack.negotiationFinished(true);
			return;
		}
		//Try negotiating a required Feature
		else if (tryNegotiateFeature(requiredFeatures)) {
			return;
		}
		//Negotiation failed if no required feature can be negotiated
		else {
			ProtocolErrorInterface error = new StreamProtocolError(StreamErrorCondition.UNSUPPORED_FEATURE, "can't negotiate required features");
			notifyObservers(error);
		}
	}

	/**
	 * Method to process negotiation data, gives the data to the current negotiator and checks whether negotiation is finished
	 *
	 * @param data negotiation data sent by the server
	 */
	private void negotiateSession(DataElementInterface data) {
		DataElementInterface response = this.currentNegotiator.negotiate(data);
		if (this.currentNegotiator.isNegotiationFinished() && !this.currentNegotiator.hasNegotiationFailed() && this.currentFeature.isRequired()) {
			// Open new Stream after a required feature has been negotiated
			StreamHeaderInterface streamHeader = new StreamHeader(this.jid);
			this.getLower().pushDataDown(streamHeader);
		} else if (this.currentNegotiator.isNegotiationFinished() && !this.currentFeature.isRequired() && this.currentFeatures.isEmpty()) {
			// Negotiation finished after negotiating the last feature, if it was optional
			this.finished = true;
			this.connectionStack.negotiationFinished(true);
		} else if (this.currentFeature.isRequired() && this.currentNegotiator.hasNegotiationFailed() && this.currentFeatures.isEmpty()) {
			// Negotiation failed if negotiating the last required feature failed
			this.connectionStack.negotiationFinished(false);
		} else if (this.currentNegotiator.isNegotiationFinished()) {
			// Start negotiating the next feature if this feature is finished and none of the above cases are true
			startNegotiation();
		} else {
			// Send negotiation response to the Server if negotiation is not finished
			this.getLower().pushDataDown(response);
		}
	}

	@Override
	public void terminateSession() {
		if (!isDisconnecting()) {
			// We didn't send the closing tag yet.
			StreamFooterInterface streamFooter = new StreamFooter();
			this.getLower().pushDataDown(streamFooter);
		}
		setDisconnecting(true);
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
	public void tryRegisterPlugin(PluginInterface plugin, boolean lastPlugin) {
		if (plugin instanceof SessionPluginInterface) {
			NegotiatorInterface sessionPlugin = (NegotiatorInterface) plugin;
			this.negotiators.add(sessionPlugin);
		}
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
	 * @param data the data sent by the Server
	 */
	@Override
	public void pushDataUp(DataElementInterface data) {
		if (data instanceof NegotiationFeatureSetInterface) {
			NegotiationFeatureSetInterface negotiationFeatures = (NegotiationFeatureSetInterface) data;
			this.currentFeatures = negotiationFeatures.getFeatures();
			this.startNegotiation();
		} else if (data instanceof StreamHeaderInterface) {
			StreamHeaderInterface responseHeader = (StreamHeaderInterface) data;
			if (!responseHeader.getTo().equals(this.jid.getFullJID())) {
				ProtocolErrorInterface error = new StreamProtocolError(StreamErrorCondition.HOST_UNKNOWN, "Unexpected recipient in the response header");
				notifyObservers(error);
			} else if (!responseHeader.getFrom().equals(this.jid.getDomainPart())) {
				ProtocolErrorInterface error = new StreamProtocolError(StreamErrorCondition.INVALID_FROM, "Unexpected sender in the response header");
				notifyObservers(error);
			} else if (!responseHeader.getNamespace().equals("jabber:server")) {
				ProtocolErrorInterface error = new StreamProtocolError(StreamErrorCondition.INVALID_NAMESPACE, "Unexpected namespace in the response header");
				notifyObservers(error);
			}
		}
		else if (data instanceof StreamFooterInterface || data instanceof ErrorElement) {
			this.connectionStack.pushDataUp(data);
		}
		else {
			negotiateSession(data);
		}
	}
}
