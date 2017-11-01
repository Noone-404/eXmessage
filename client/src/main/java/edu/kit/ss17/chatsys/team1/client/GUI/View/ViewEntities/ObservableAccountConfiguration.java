package edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities;

import com.sun.javafx.binding.ExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.Contract;

import java.util.function.Consumer;

/**
 *
 */
public class ObservableAccountConfiguration implements ObservableValue<AccountConfigurationInterface> {

	private ExpressionHelper<AccountConfigurationInterface> helper;
	private boolean                                         lastInvalidated;
	private ObservableBaseAccountConfiguration              accountConfiguration;
	private Consumer<Exception>                             changeErrorFeedbackCallback;

	public ObservableAccountConfiguration(AccountConfigurationInterface accountConfiguration) {
		this.accountConfiguration = new ObservableBaseAccountConfiguration(accountConfiguration);
	}

	@Override
	public void addListener(ChangeListener<? super AccountConfigurationInterface> listener) {
		this.helper = ExpressionHelper.addListener(this.helper, this, listener);
	}

	@Override
	public void removeListener(ChangeListener<? super AccountConfigurationInterface> listener) {
		this.helper = ExpressionHelper.removeListener(this.helper, listener);
	}

	@Override
	public ObservableBaseAccountConfiguration getValue() {
		this.lastInvalidated = false;
		return this.accountConfiguration;
	}

	public void setChangeErrorFeedbackCallback(Consumer<Exception> errorFeedbackCallback) {
		this.changeErrorFeedbackCallback = errorFeedbackCallback;
	}

	public void changeFailedFeedback(Exception e) {
		if (this.changeErrorFeedbackCallback != null)
			this.changeErrorFeedbackCallback.accept(e);
	}

	private void invalidate() {
		if (this.lastInvalidated)
			return;

		this.lastInvalidated = true;
		ExpressionHelper.fireValueChangedEvent(this.helper);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		this.helper = ExpressionHelper.addListener(this.helper, this, listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		this.helper = ExpressionHelper.removeListener(this.helper, listener);
	}

	public final class ObservableBaseAccountConfiguration extends AccountConfiguration implements ObservableAccountConfigurationInterface {

		/**
		 * Helper constructor to create an ObservableBaseAccountConfiguration-object from AccountConfigurationInterface
		 */
		private ObservableBaseAccountConfiguration(AccountConfigurationInterface accountConfiguration) {
			super.setAddress(accountConfiguration.getAddress());
			super.setJid(accountConfiguration.getJid());
			super.setPassword(accountConfiguration.getPassword());
			super.setPort(accountConfiguration.getPort());
			super.setProtocol(accountConfiguration.getProtocol());
		}

		private void onFetchValue() {
			ObservableAccountConfiguration.this.lastInvalidated = false;
		}

		private void onValueChanged() {
			invalidate();
		}

		@Contract(value = " -> !null", pure = true)
		@Override
		public Observable getObservable() {
			return ObservableAccountConfiguration.this;
		}

		@Override
		public String getAddress() {
			onFetchValue();
			return super.getAddress();
		}

		@Override
		public String getJid() {
			onFetchValue();
			return super.getJid();
		}

		@Override
		public String getPassword() {
			onFetchValue();
			return super.getPassword();
		}

		@Override
		public int getPort() {
			onFetchValue();
			return super.getPort();
		}

		@Override
		public String getProtocol() {
			onFetchValue();
			return super.getProtocol();
		}

		@Override
		public void setAddress(String address) {
			if (address.equals(super.getAddress()))
				return;

			super.setAddress(address);
			onValueChanged();
		}

		@Override
		public void setPort(int port) {
			if (port == super.getPort())
				return;

			super.setPort(port);
			onValueChanged();
		}

		@Override
		public void setProtocol(String protocol) {
			if (protocol.equals(super.getProtocol()))
				return;

			super.setProtocol(protocol);
			onValueChanged();
		}

		@Override
		public void setJid(String jid) {
			if (jid.equals(super.getJid()))
				return;

			super.setJid(jid);
			onValueChanged();
		}

		@Override
		public void setPassword(String password) {
			if (password.equals(super.getPassword()))
				return;

			super.setPassword(password);
			onValueChanged();
		}
	}
}
