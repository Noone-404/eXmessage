package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import edu.kit.ss17.chatsys.team1.client.GUI.SaveAccountConfigurationFunction;
import edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities.AccountConfigurationInterface;

import java.util.List;

/**
 *
 */
public interface AccountConfigurationDialogInterface extends FormBaseInterface {

	void showForAccount(AccountConfigurationInterface aci);
	void showConfigurationChangeFailedMessage(Exception reason);
	void setAvailableProtocols(List<String> protocols);
	void setAccountConfigurationAddedConsumer(SaveAccountConfigurationFunction consumer);
}
