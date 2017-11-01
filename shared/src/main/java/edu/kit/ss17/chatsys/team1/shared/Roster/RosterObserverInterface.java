package edu.kit.ss17.chatsys.team1.shared.Roster;

import edu.kit.ss17.chatsys.team1.shared.Util.DesignHelpers.ObserverInterface;

/**
 *
 */
public interface RosterObserverInterface extends ObserverInterface {

	void contactCreated(RosterInterface roster, ContactInterface contact);

	void contactRemoved(RosterInterface roster, ContactInterface contact);

	default void observerRegistered(RosterInterface roster) {
	}
}
