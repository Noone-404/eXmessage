package edu.kit.ss17.chatsys.team1.plugins.TCPConnectionPlugin;

import java.net.Socket;

/**
 * Default implementation of {@Link SocketConnectionIdentificationInterface}.
 */
class SocketConnectionIdentification implements SocketConnectionIdentificationInterface {

	private final Socket socket;

	SocketConnectionIdentification(Socket socket) {
		this.socket = socket;
	}

	@Override
	public Socket getSocket() {
		return this.socket;
	}

	@Override
	public String getAddress() {
		return this.socket.getInetAddress().toString();
	}

	@Override
	public int getPort() {
		return this.socket.getPort();
	}
}
