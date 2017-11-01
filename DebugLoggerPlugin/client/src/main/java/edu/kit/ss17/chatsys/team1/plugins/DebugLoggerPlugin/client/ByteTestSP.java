package edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.client;

import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.MessageInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamProcessor.Plugins.DataElementPluginInterface;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static edu.kit.ss17.chatsys.team1.shared.Constants.PLUGIN_LOGGERS;

/**
 * Responsible for sending and receiving test bytes at the StreamProcessor.
 */
public class ByteTestSP implements DataElementPluginInterface {

	private static final Logger          logger  = LogManager.getLogger(PLUGIN_LOGGERS);
	private final PluginSetInterface pluginSet;
	private final GuiMenuPlugin      guiMenuPlugin;
	byte[] messageHash;
	private              BooleanProperty enabled = new SimpleBooleanProperty(false);

	/**
	 * Constructor of the ByteTestSP class.
	 *
	 * @param pluginSet     the PluginSet this plugin belongs to.
	 * @param guiMenuPlugin the guiMenuPlugin to communicate with.
	 */
	public ByteTestSP(PluginSetInterface pluginSet, GuiMenuPlugin guiMenuPlugin) {
		this.pluginSet = pluginSet;
		this.guiMenuPlugin = guiMenuPlugin;
	}

	/**
	 * Calculates the sha256 hash of a byte array.
	 */
	private byte[] sha256digest(byte[] data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.reset();
		digest.update(data);
		return digest.digest();
	}

	private String tohex(byte[] data) {
		StringBuilder sb = new StringBuilder(data.length * 2);
		for (int i = 0; i < data.length; i++) {
			sb.append(String.format("%02X", data[i] & 0xFF));
		}
		return sb.toString();
	}

	/**
	 * Checks if the DataElement is a test message.
	 */
	private boolean isTestMessage(DataElementInterface data) {
		if (data instanceof MessageInterface) {
			try {
				String prefix      = "###" + this.pluginSet.getName() + " Test###";
				byte[] prefixBytes = prefix.getBytes("UTF-8");

				byte[] message = ((MessageInterface) data).getPlaintextRepresentation().getBytes(("UTF-8"));

				if (message.length >= prefixBytes.length) {
					byte[] messagePrefix = Arrays.copyOfRange(message, 0, prefixBytes.length);

					return (Arrays.equals(messagePrefix, prefixBytes));
				}
			} catch (UnsupportedEncodingException e) {
				return false;
			}
		}

		return false;
	}

	@Override
	public Collection<DataElementInterface> incomingDataElement(DataElementInterface data) {
		if (isTestMessage(data)) {
			// Gui just sent a test message.
			try {
				byte[] message = ((MessageInterface) data).getPlaintextRepresentation().getBytes(("UTF-8"));

				// Calculate hash and notify GuiMenuPlugin.
				this.messageHash = sha256digest(message);
				this.guiMenuPlugin.updateByteTestStatus("Message stanza (" + message.length + " Byte) going through StreamProcessor");
			} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
				System.out.println(e.getMessage());
			}
		}

		return Arrays.asList(data);
	}

	@Override
	public Collection<DataElementInterface> outgoingDataElement(DataElementInterface data) {
		if (isTestMessage(data)) {
			// We received a test message from the server.
			if (this.messageHash != null) {
				try {
					byte[] message = ((MessageInterface) data).getPlaintextRepresentation().getBytes(("UTF-8"));

					// Calculate hash and notify GuiMenuPlugin.
					if (Arrays.equals(this.messageHash, sha256digest(message))) {
						this.guiMenuPlugin.setByteTestResult(true);
						logger.info("Byte sending test successfull");
					} else {
						this.guiMenuPlugin.setByteTestResult(false);
						logger.info("Byte sending test failed");
						logger.info("\nSent: " + this.tohex(this.messageHash) + "\nReceived: " + this.tohex(sha256digest(message)));
					}

					this.guiMenuPlugin.updateByteTestStatus("Message stanza (" + message.length + " Byte) received from server");
				} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
					System.out.println(e.getMessage());
				}
			}

			this.messageHash = null;

			// Don't forward test message to gui.
			return new ArrayList<>();
		}
		return Arrays.asList(data);
	}

	@Override
	public BooleanProperty getEnabledProperty() {
		return this.enabled;
	}

	@Override
	public int getWeight() {
		return 0;
	}

	@Override
	public PluginSetInterface getPluginSet() {
		return this.pluginSet;
	}

	@Override
	public void setPluginSet(PluginSetInterface pluginSet) {
		// do nothing
	}

	@Override
	public void registerErrorObserver(ProtocolErrorObserverInterface observer) {
		// do nothing
	}

	@Override
	public void unregisterErrorObserver(ProtocolErrorObserverInterface observer) {
		// do nothing
	}
}
