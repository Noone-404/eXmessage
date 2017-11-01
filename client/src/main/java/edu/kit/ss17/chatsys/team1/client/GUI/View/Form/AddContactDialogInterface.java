package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import edu.kit.ss17.chatsys.team1.client.GUI.AddContactFunction;

import java.util.Collection;
import java.util.function.Consumer;

/**
 *
 */
public interface AddContactDialogInterface extends FormBaseInterface, AutoCloseableFormInterface {

	void setSearchRequestCallback(Consumer<String> consumer);
	void setContactAddCallback(AddContactFunction consumer);
	void provideSearchResults(Collection<String> results);
	void setCurrentContacts(Collection<String> currentContacts);
}
