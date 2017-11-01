package edu.kit.ss17.chatsys.team1.shared.StreamData;

import java.time.Instant;

/**
 * This abstract class implements some of the {@link StanzaInterface} functionality.
 * Every class that implements {@link StanzaInterface} should extend this class.
 */
public abstract class AbstractStanza implements StanzaInterface {

	private String  id = "";
	private String  sender = "";
	private String  receiver = "";
	private Instant clientSendDate;
	private Instant serverReceiveDate;
	private Instant clientReceiveDate;
	private String  type = "";

	@Override
	public String getID() {
		return this.id;
	}

	@Override
	public void setID(String id) {
		this.id = id;
	}

	@Override
	public String getSender() {
		return this.sender;
	}

	@Override
	public void setSender(String sender) {
		this.sender = sender;
	}

	@Override
	public String getReceiver() {
		return this.receiver;
	}

	@Override
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	@Override
	public Instant getClientSendDate() {
		return this.clientSendDate;
	}

	@Override
	public void setClientSendDate(Instant clientSendDate) {
		this.clientSendDate = clientSendDate;
	}

	@Override
	public Instant getServerReceiveDate() {
		return this.serverReceiveDate;
	}

	@Override
	public void setServerReceiveDate(Instant serverReceiveDate) {
		this.serverReceiveDate = serverReceiveDate;
	}

	@Override
	public Instant getClientReceiveDate() {
		return this.clientReceiveDate;
	}

	@Override
	public void setClientReceiveDate(Instant clientReceiveDate) {
		this.clientReceiveDate = clientReceiveDate;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "AbstractStanza{" +
		       "id='" + this.id + '\'' +
		       ", sender='" + this.sender + '\'' +
		       ", receiver='" + this.receiver + '\'' +
		       ", clientSendDate=" + this.clientSendDate +
		       ", serverReceiveDate=" + this.serverReceiveDate +
		       ", clientReceiveDate=" + this.clientReceiveDate +
		       ", type='" + this.type + '\'' +
		       '}';
	}
}
