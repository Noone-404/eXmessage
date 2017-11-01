package edu.kit.ss17.chatsys.team1.server.Controller;

/**
 * Interface for a central controller, which is responsible for creating the connection stack.
 */
public interface ControllerInterface {

	void closeConnectionStack(ConnectionStackInterface stack);
}
