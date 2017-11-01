package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import com.sun.glass.ui.Size;
import edu.kit.ss17.chatsys.team1.client.GUI.SaveAccountConfigurationFunction;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.AccountConfiguration;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.AccountConfigurationInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.JID.InvalidJIDException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List;

/**
 *
 */
public class AccountConfigurationDialog extends FormBase implements AccountConfigurationDialogInterface {

	private static final Size DEFAULT_SIZE = new Size(500, 350);

	@FXML
	private Button           btSave;
	@FXML
	private TextField        tbAddress;
	@FXML
	private Spinner<Integer> isPort;
	@FXML
	private ComboBox<String> cbProtocol;
	@FXML
	private TextField        tbJid;
	@FXML
	private PasswordField    tbPassword;

	private boolean                       editing;
	private boolean                       changeFailed;
	private AccountConfigurationInterface currentConfiguration;

	private SaveAccountConfigurationFunction accountConfigurationAddedConsumer;

	public AccountConfigurationDialog() {
		getStage().setOnCloseRequest(this::closeConfirmation);
		this.cbProtocol.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			if ((KeyCode.UP == e.getCode() || KeyCode.DOWN == e.getCode()) && e.getTarget() instanceof ComboBox) {
				ComboBox cb = (ComboBox) e.getTarget();
				cb.getSelectionModel().select(cb.getSelectionModel().getSelectedIndex() + 1);
			}
		});
	}

	private static boolean checkSameValue(String old, String now) { // Utility method to easier handle null values
		return old == null && now == null ||
		       now != null && now.equals(old) ||
		       old != null && old.equals(now);
	}

	@Override
	public void show() {
		this.editing = false;
		this.currentConfiguration = new AccountConfiguration();
		initFields();
		super.show();
	}

	@Override
	public void showForAccount(AccountConfigurationInterface aci) {
		this.editing = true;
		this.currentConfiguration = aci;
		initFields();
		super.show();
	}

	@Override
	public void setAccountConfigurationAddedConsumer(SaveAccountConfigurationFunction consumer) {
		this.accountConfigurationAddedConsumer = consumer;
	}

	@Override
	public void setAvailableProtocols(List<String> protocols) {
		this.cbProtocol.getItems().setAll(protocols);
	}

	@Override
	public void showConfigurationChangeFailedMessage(Exception reason) {
		if (this.changeFailed)
			return;

		this.changeFailed = true;

		String reasonSuffix = reason instanceof InvalidJIDException ? "jid" : "general";

		new AutoCloseableAlert(
				AlertType.ERROR,
				getLocaleResource().getString("acc_save_failed_title_" + reasonSuffix),
				getLocaleResource().getString("acc_save_failed_header_" + reasonSuffix),
				getLocaleResource().getString("acc_save_failed_" + reasonSuffix),
				ButtonType.OK)
				.showAndWait();
	}

	@Override
	protected Size getDefaultSize() {
		return DEFAULT_SIZE;
	}

	@Override
	protected String getTitle() {
		return getLocaleResource().getString(this.editing ? "acc_edit" : "acc_create");
	}

	@SuppressWarnings("unused")
	@FXML
	private void btCancel(ActionEvent event) {
		closeConfirmation(event);

		if (!event.isConsumed())
			getStage().close();
	}

	@SuppressWarnings("unused")
	@FXML
	private void btSave(ActionEvent event) {
		String prevJid      = this.currentConfiguration.getJid();
		String prevAddress  = this.currentConfiguration.getAddress();
		String prevProtocol = this.currentConfiguration.getProtocol();
		String prevPassword = this.currentConfiguration.getPassword();
		int    prevPort     = this.currentConfiguration.getPort();

		this.currentConfiguration.setJid(this.tbJid.getText());
		this.currentConfiguration.setAddress(this.tbAddress.getText());
		this.currentConfiguration.setPort(this.isPort.getValue());
		this.currentConfiguration.setProtocol(this.cbProtocol.getValue());
		this.currentConfiguration.setPassword(this.tbPassword.getText());

		if (!this.editing && this.accountConfigurationAddedConsumer != null)
			try {
				this.accountConfigurationAddedConsumer.accept(this.currentConfiguration);
			} catch (InvalidJIDException e) {
				this.showConfigurationChangeFailedMessage(e);
			}

		if (this.changeFailed) {
			// revert changes
			this.currentConfiguration.setJid(prevJid);
			this.currentConfiguration.setAddress(prevAddress);
			this.currentConfiguration.setPort(prevPort);
			this.currentConfiguration.setProtocol(prevProtocol);
			this.currentConfiguration.setPassword(prevPassword);

			// reset flag
			this.changeFailed = false; // sneaky, suppressing errors during reversion
		} else {
			new AutoCloseableAlert(
					AlertType.INFORMATION,
					getLocaleResource().getString("acc_save_ok_title"),
					getLocaleResource().getString("acc_save_ok_header"),
					getLocaleResource().getString("acc_save_ok"),
					ButtonType.OK)
					.showAndWait();

			getStage().close();
		}
	}

	private void initFields() {
		this.tbAddress.setText(this.currentConfiguration.getAddress());
		this.isPort.getValueFactory().setValue(this.currentConfiguration.getPort());
		if (this.cbProtocol.getItems().contains(this.currentConfiguration.getProtocol()))
			this.cbProtocol.setValue(this.currentConfiguration.getProtocol());
		else {
			this.cbProtocol.getSelectionModel().clearSelection();
			this.cbProtocol.setValue("");
		}

		this.tbJid.setText(this.currentConfiguration.getJid());
		this.tbPassword.setText(this.currentConfiguration.getPassword());
	}

	private void closeConfirmation(Event event) {
		if (this.isPort.getValue() == this.currentConfiguration.getPort() &&
		    checkSameValue(this.currentConfiguration.getAddress(), this.tbAddress.getText()) &&
		    checkSameValue(this.cbProtocol.getValue(), this.currentConfiguration.getProtocol()) &&
		    checkSameValue(this.tbJid.getText(), (this.currentConfiguration.getJid())) &&
		    checkSameValue(this.tbPassword.getText(), (this.currentConfiguration.getPassword())))
			return;

		if (!new AutoCloseableAlert(
				AlertType.CONFIRMATION,
				getLocaleResource().getString(this.editing ? "acc_confirmation_title" : "acc_confirmation_title_new"),
				getLocaleResource().getString(this.editing ? "acc_confirmation_caption" : "acc_confirmation_caption_new"),
				getLocaleResource().getString(this.editing ? "acc_confirmation" : "acc_confirmation_new"),
				ButtonType.CANCEL,
				ButtonType.YES, ButtonType.NO, ButtonType.CANCEL)
				.showAndWait()
				.filter(ButtonType.YES::equals).isPresent())
			event.consume();
	}
}
