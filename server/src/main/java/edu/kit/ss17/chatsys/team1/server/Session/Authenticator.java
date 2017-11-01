package edu.kit.ss17.chatsys.team1.server.Session;

import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeature;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureOption;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureOptionInterface;
import edu.kit.ss17.chatsys.team1.shared.Storage.StorageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationRequest;
import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationResponse;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Class to negotiate authentication with the client.
 */
public class Authenticator implements AuthenticatorInterface {

	private static final String  FEATURE_NAME          = "simple-Authentication";
	private static final boolean REQUIRED              = true;
	private static final String  MECHANISM_OPTION_NAME = "mechanism";
	private static final String  DEFAULT_MECHANISM     = "simple-Password-Authentication";
	private final StorageInterface storage;
	private final int              weight;
	private final Collection<NegotiationFeatureOptionInterface> options;
	private       JID              jid;
	private       boolean          negotiationFinished;
	private       boolean          negotiationFailed;

	/**
	 * constructor with storage
	 *
	 * @param storage Storage class that allows access to the account information (JID + passsword)
	 */
	public Authenticator(StorageInterface storage) {
		this.storage = storage;
		this.weight = 10;
		this.negotiationFinished = false;
		this.negotiationFailed = false;
		Collection<NegotiationFeatureOptionInterface> options    = new ArrayList<>();
		Collection<String>                            mechanisms = new ArrayList<>();
		mechanisms.add(DEFAULT_MECHANISM);
		NegotiationFeatureOptionInterface mechanismOption = new NegotiationFeatureOption(MECHANISM_OPTION_NAME, mechanisms);
		options.add(mechanismOption);
		this.options = options;
	}


	@Override
	@Nullable
	public DataElementInterface negotiate(DataElementInterface input) {
		if (input instanceof AuthenticationRequest) {
			AuthenticationRequest authenticationRequest = (AuthenticationRequest) input;
			JID                   requestJID            = new JID();
			requestJID.setLocalPart(authenticationRequest.getJID().getLocalPart());
			requestJID.setDomainPart(authenticationRequest.getJID().getDomainPart());
			String           password = authenticationRequest.getPassword();
			AccountInterface account  = this.storage.getAccount(requestJID);
			if (account == null) {
				this.negotiationFailed = true;
				this.negotiationFinished = true;
				return new AuthenticationResponse(false);
			} else {
				if (password.equals(account.getPassword())) {
					this.negotiationFinished = true;
					this.negotiationFailed = false;
					this.jid = authenticationRequest.getJID();
					return new AuthenticationResponse(true);
				} else {
					this.negotiationFailed = true;
					this.negotiationFinished = true;
					return new AuthenticationResponse(false);
				}
			}
		} else {
			return null;
		}
	}

	@Override
	public boolean isNegotiationFinished() {
		return this.negotiationFinished;
	}

	@Override
	public boolean hasNegotiationFailed() {
		return this.negotiationFailed;
	}

	@Override
	public boolean isRequired() {
		return REQUIRED;
	}

	@Override
	public NegotiationFeature getNegotiationFeature() {
		return new NegotiationFeature(FEATURE_NAME, this.options, REQUIRED);
	}

	@Override
	public int getWeight() {
		return this.weight;
	}

	@Override
	public JID getJid() {
		if (this.jid == null) {
			throw new IllegalStateException("No JID has been successfully authenticated yet");
		} else {
			return this.jid;
		}
	}
}


