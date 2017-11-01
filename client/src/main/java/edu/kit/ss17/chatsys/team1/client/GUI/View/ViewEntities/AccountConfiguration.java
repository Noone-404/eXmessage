package edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities;

import com.sun.javafx.binding.ExpressionHelper;

import static edu.kit.ss17.chatsys.team1.shared.Constants.DEFAULT_PORT;

/**
 *
 */
public class AccountConfiguration implements AccountConfigurationInterface {

	private String address;
	private int    port;
	private String protocol;
	private String jid;
	private String password;

	private ExpressionHelper<AccountConfigurationInterface> helper;
	private boolean                                         lastInvalidated;


	public AccountConfiguration() {
		this.port = DEFAULT_PORT;
		this.address = "";
		this.protocol = "";
		this.jid = "";
		this.password = "";
	}

	public AccountConfiguration(String address, int port, String protocol, String jid, String password) {
		this.address = address;
		this.port = port;
		this.protocol = protocol;
		this.jid = jid;
		this.password = password;
	}

	@Override
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public void setJid(String jid) {
		this.jid = jid;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getAddress() {
		return this.address;
	}

	@Override
	public int getPort() {
		return this.port;
	}

	@Override
	public String getProtocol() {
		return this.protocol;
	}

	@Override
	public String getJid() {
		return this.jid;
	}

	@Override
	public String getPassword() {
		return this.password;
	}
}
