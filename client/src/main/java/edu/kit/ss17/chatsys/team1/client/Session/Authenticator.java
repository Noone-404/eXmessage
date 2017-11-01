package edu.kit.ss17.chatsys.team1.client.Session;

import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureInterface;
import edu.kit.ss17.chatsys.team1.shared.Negotiation.NegotiationFeatureOptionInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationRequest;
import edu.kit.ss17.chatsys.team1.shared.StreamData.AuthenticationResponse;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.Account.AccountInterface;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * class to negotiate authentication with the server
 */
public class Authenticator implements NegotiatorInterface {

	private static final String FEATURE_NAME          = "simple-Authentication";
	private static final String MECHANISM_OPTION_NAME = "mechanism";
	private static final String DEFAULT_MECHANISM     = "simple-Password-Authentication";
	private final AccountInterface account;
	private       boolean          negotiationFinished;
	private       boolean          negotiationFailed;

	/**
	 * constructor with account object for the account that needs to be authenticated
	 */
	public Authenticator(AccountInterface acc) {
		this.account = acc;
		this.negotiationFinished = false;
		this.negotiationFailed = false;
	}


	@Override
	@Nullable
	public DataElementInterface negotiate(DataElementInterface input) {
		if (input instanceof AuthenticationResponse) {
			AuthenticationResponse response = (AuthenticationResponse) input;
			this.negotiationFinished = true;
			this.negotiationFailed = !response.getAuthenticationSuccess();
		}
		return null;
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
	@Nullable
	public DataElementInterface startNegotiation(NegotiationFeatureInterface feature) {
		if (feature.getName().equals(FEATURE_NAME)) {
			Collection<NegotiationFeatureOptionInterface> options    = feature.getOptions();
			Collection<String>                            mechanisms = null;
			for (final NegotiationFeatureOptionInterface currentOption : options) {
				if (currentOption.getOptionName().equals(MECHANISM_OPTION_NAME)) {
					mechanisms = currentOption.getValues();
				}
			}
			if (mechanisms == null) {
				return null;
			} else {
				if (mechanisms.contains(DEFAULT_MECHANISM)) {
					return new AuthenticationRequest(this.account.getJid(), this.account.getPassword());
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}
}
