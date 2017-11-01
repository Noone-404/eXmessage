package edu.kit.ss17.chatsys.team1.client.GUI.View.ViewEntities;

/**
 *
 */
public interface AccountConfigurationInterface {

	void setAddress(String address);
	void setPort(int port);
	void setProtocol(String protocol);
	void setJid(String jid);
	void setPassword(String password);

	String getAddress();
	int getPort();
	String getProtocol();
	String getJid();
	String getPassword();
}
