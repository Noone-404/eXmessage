package edu.kit.ss17.chatsys.team1.shared.Util;

import edu.kit.ss17.chatsys.team1.shared.StreamData.StanzaInterface;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Default ID generator, which just uses the current timestamp and a random number for generation.
 */
public class IDGenerator implements IDGeneratorInterface {

	private static IDGenerator instance;

	private IDGenerator() {
	}

	public static IDGenerator getInstance() {
		return instance != null ? instance : (instance = new IDGenerator());
	}

	@Nullable
	@Override
	public String getID(StanzaInterface stanza) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ignored) {
			return null;
		}

		String nano = String.valueOf(System.nanoTime());
		String rand = String.valueOf(Math.random());

		byte[]        messageDigest = md.digest((nano + rand).getBytes());
		StringBuilder hexString     = new StringBuilder();
		for (byte aMessageDigest : messageDigest) {
			String hex = Integer.toHexString(0xFF & aMessageDigest);
			if (hex.length() == 1) {
				// could use a for loop, but we're only dealing with a single
				// byte
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}
}
