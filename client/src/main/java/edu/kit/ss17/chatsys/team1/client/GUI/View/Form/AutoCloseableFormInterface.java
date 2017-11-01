package edu.kit.ss17.chatsys.team1.client.GUI.View.Form;

import edu.kit.ss17.chatsys.team1.client.GUI.View.Form.AutoCloseableFormInterface.AutoCloseableFormObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObservableInterface;
import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObserverInterface;

/**
 *
 */
public interface AutoCloseableFormInterface extends ObservableInterface<AutoCloseableFormObserverInterface> {

	void close();

	@FunctionalInterface
	interface AutoCloseableFormObserverInterface extends ObserverInterface {

		void closed();
	}
}
