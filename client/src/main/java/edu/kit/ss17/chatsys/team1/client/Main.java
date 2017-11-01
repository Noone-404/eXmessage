package edu.kit.ss17.chatsys.team1.client;


import edu.kit.ss17.chatsys.team1.client.Controller.Controller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static edu.kit.ss17.chatsys.team1.shared.Constants.APP_NAME;

public class Main {

	private static final Logger logger = LogManager.getLogger(APP_NAME);

	public static void main(final String[] args) {
		logger.info("application started");

		Controller.getInstance().runApplication();
	}
}
