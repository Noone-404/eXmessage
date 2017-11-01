package edu.kit.ss17.chatsys.team1.shared.Util;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;

/**
 * Class to create a comparable, hashable JID object with atomic parts.
 */
@Embeddable
public class JID implements Serializable { // TODO: JID-Interface definieren

	public static final char DOMAIN_SEPARATOR   = '@';
	public static final char RESOURCE_SEPARATOR = '/';

	protected String localPart    = "";
	protected String domainPart   = "";
	protected String resourcePart = "";

	public JID() {
	}

	/**
	 * Creates a JID object
	 *
	 * @param jid The full JID String with optional resource part, e.g. test@jabber.de/home.
	 *
	 * @throws IllegalArgumentException if the provided JID is invalid.
	 */
	public JID(String jid) throws InvalidJIDException {
		intSetFullJID(jid);
	}

	/**
	 * Get the local part of the jid local@domain/resource
	 */
	public String getLocalPart() {
		return this.localPart;
	}

	/**
	 * Set the local part of the jid
	 *
	 * @param localPart the local part
	 */
	public void setLocalPart(String localPart) {
		this.localPart = localPart;
	}

	/**
	 * Get the domain part of the jid local@domain/resource
	 */
	public String getDomainPart() {
		return this.domainPart;
	}

	/**
	 * Set the domain part of the jid
	 *
	 * @param domainPart the domain part
	 */
	public void setDomainPart(String domainPart) {
		this.domainPart = domainPart;
	}

	/**
	 * Get the resource part of the jid local@domain/resource
	 */
	public String getResourcePart() {
		return this.resourcePart;
	}

	/**
	 * Set the resource part of the jid
	 *
	 * @param resourcePart the resource part
	 */
	public void setResourcePart(String resourcePart) {
		this.resourcePart = resourcePart;
	}

	/**
	 * Get the full JID incl. resource part if set.
	 *
	 * @throws IllegalStateException
	 */
	@Transient
	public String getFullJID() {
		if (getLocalPart().isEmpty() || getDomainPart().isEmpty())
			throw new IllegalStateException("Local and domain part of JID required");

		String jid = getLocalPart() + DOMAIN_SEPARATOR + getDomainPart();
		if (!getResourcePart().isEmpty())
			return jid + RESOURCE_SEPARATOR + getResourcePart();

		return jid;
	}

	/**
	 * Sets the full jid by dividing the jid into its minor parts.
	 *
	 * @param jid the full JID incl. resource part.
	 */
	@Transient
	public void setFullJID(String jid) throws InvalidJIDException {
		intSetFullJID(jid);
	}

	private void intSetFullJID(String jid) throws InvalidJIDException {
		String[] parts = jid.split("@");
		if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty())
			throw new InvalidJIDException();

		this.localPart = parts[0];

		parts = parts[1].split("/");
		if (parts.length > 2 || parts[0].isEmpty())
			throw new InvalidJIDException();
		else if (parts.length == 2 && !parts[1].isEmpty())
			this.resourcePart = parts[1];

		this.domainPart = parts[0];
	}

	/**
	 * Gets the base JID without resource part.
	 */
	@Transient
	public String getBaseJID() {
		if (getLocalPart().isEmpty() || getDomainPart().isEmpty())
			throw new IllegalStateException("Local and domain part of JID required");

		return getLocalPart() + DOMAIN_SEPARATOR + getDomainPart();
	}

	@Override
	public String toString() {
		return this.getFullJID();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getFullJID());
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof JID && Objects.equals(getFullJID(), ((JID) obj).getFullJID());
	}

	public static class InvalidJIDException extends Exception {

		private static final long serialVersionUID = -2154861636535136482L;

		InvalidJIDException() {
			super();
		}

		InvalidJIDException(String message) {
			super(message);
		}

		InvalidJIDException(Throwable throwable) {
			super(throwable);
		}

		InvalidJIDException(String message, Throwable throwable) {
			super(message, throwable);
		}
	}
}
