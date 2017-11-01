package edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.client;

import edu.kit.ss17.chatsys.team1.client.GUI.Plugin.GuiMenuPluginInterface;
import edu.kit.ss17.chatsys.team1.client.GUI.Plugin.PluginAccessor.GuiPluginAccessorInterface;
import edu.kit.ss17.chatsys.team1.client.Model.Contact;
import edu.kit.ss17.chatsys.team1.client.Model.ContentInterface;
import edu.kit.ss17.chatsys.team1.client.Model.PlainTextContentFragment;
import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared.DataElementObserverInterface;
import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared.DataElementSP;
import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared.DocumentObserverInterface;
import edu.kit.ss17.chatsys.team1.plugins.DebugLoggerPlugin.shared.DocumentSP;
import edu.kit.ss17.chatsys.team1.shared.PluginManager.PluginSetInterface;
import edu.kit.ss17.chatsys.team1.shared.ProtocolError.ProtocolErrorObserverInterface;
import edu.kit.ss17.chatsys.team1.shared.Roster.ContactInterface;
import edu.kit.ss17.chatsys.team1.shared.StreamData.DataElementInterface;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.dom4j.Document;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

/**
 * Creates a Gui menu entry to open the debug window.
 */
public class GuiMenuPlugin implements GuiMenuPluginInterface {

	private final String     NEWLINE    = "\n";
	private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
	private final PluginSetInterface         pluginSet;
	private final DataElementSP              dataElementPlugin;
	private final DocumentSP                 documentPlugin;
	private       GuiPluginAccessorInterface guiAccessor;
	private BooleanProperty enabled = new SimpleBooleanProperty(false);

	// Controls
	private TextArea textArea;
	private CheckBox incomingDataElementCB;
	private CheckBox outgoingDataElementCB;
	private CheckBox incomingDocumentCB;
	private CheckBox outgoingDocumentCB;

	private TextField msgNumber;
	private Button    btSendMessages;

	private TextField byteQuantity;
	private Text      byteTestStatus = new Text("");
	private Text      byteTestResult = new Text("");
	private Button    btSendBytes;

	public GuiMenuPlugin(PluginSetInterface pluginSet, DataElementSP dataElementPlugin, DocumentSP documentPlugin) {
		this.pluginSet = pluginSet;
		this.dataElementPlugin = dataElementPlugin;
		this.documentPlugin = documentPlugin;
		this.registerObservers();
	}

	/**
	 * Set the status of a running Byte sending test.
	 *
	 * @param message the status message
	 */
	public void updateByteTestStatus(String message) {
		Platform.runLater(() -> this.byteTestStatus.setText(message));
	}

	/**
	 * Set the result of the Byte sending test.
	 *
	 * @param result the result
	 */
	public void setByteTestResult(boolean result) {
		if (result)
			Platform.runLater(() -> this.byteTestResult.setText("successfull (same hashes)"));
		else
			Platform.runLater(() -> this.byteTestResult.setText("failed (different hashes)"));
	}

	private String getDate() {
		Date date = new Date();
		return "[" + dateFormat.format(date) + "]";
	}

	/**
	 * Registers its observers at the DataElementSP and DocumentSP plugins.
	 */
	private void registerObservers() {
		this.dataElementPlugin.registerObserver(new DataElementObserverInterface() {
			@Override
			public void sawIncomingDataElement(DataElementInterface data) {
				if (incomingDataElementCB != null && incomingDataElementCB.isSelected() && textArea != null) {
					textArea.appendText(getDate() + " Upper --DataElement--> StreamProcessor:" + NEWLINE);
					textArea.appendText(data.serialize().getRootElement().asXML() + NEWLINE + NEWLINE);
				}
			}

			@Override
			public void sawLeavingDataElement(DataElementInterface data) {
				if (outgoingDataElementCB != null && outgoingDataElementCB.isSelected() && textArea != null) {
					textArea.appendText(getDate() + " StreamProcessor --DataElement--> Upper:" + NEWLINE);
					textArea.appendText(data.serialize().getRootElement().asXML() + NEWLINE + NEWLINE);
				}
			}
		});

		this.documentPlugin.registerObserver(new DocumentObserverInterface() {
			@Override
			public void sawIncomingDocument(Document data) {
				if (incomingDocumentCB != null && incomingDocumentCB.isSelected() && textArea != null) {
					textArea.appendText(getDate() + " ProtocolTranslator --Document--> StreamProcessor:" + NEWLINE);
					textArea.appendText(data.getRootElement().asXML() + NEWLINE + NEWLINE);
				}
			}

			@Override
			public void sawLeavingDocument(Document data) {
				if (outgoingDocumentCB != null && outgoingDocumentCB.isSelected() && textArea != null) {
					textArea.appendText(getDate() + " StreamProcessor --Document--> ProtocolTranslator:" + NEWLINE);
					textArea.appendText(data.getRootElement().asXML() + NEWLINE + NEWLINE);
				}
			}
		});
	}

	/**
	 * Initializes and opens the debug window.
	 */
	private void openDebugWindow() {
		// Initialize containers
		VBox       vbox = new VBox(10); // Textbox
		HBox       hbox = new HBox(8); // Checkboxes
		AnchorPane pane = new AnchorPane();
		pane.getChildren().add(vbox);
		Scene scene = new Scene(pane, 1080, 800);
		Stage stage = new Stage();
		stage.setScene(scene);

		// Initialize controls
		this.textArea = new TextArea();
		this.textArea.setPrefColumnCount(80);
		this.textArea.setPrefRowCount(30);
		vbox.getChildren().add(this.textArea);
		vbox.getChildren().addAll(hbox);

		this.incomingDataElementCB = new CheckBox("DataElements -> StreamProcessor");
		this.outgoingDataElementCB = new CheckBox("StreamProcessor -> DataElements");
		this.incomingDocumentCB = new CheckBox("Documents -> StreamProcessor");
		this.outgoingDocumentCB = new CheckBox("StreamProcessor -> Documents");
		hbox.getChildren().addAll(Arrays.asList(this.incomingDataElementCB, this.outgoingDataElementCB, this.incomingDocumentCB, this.outgoingDocumentCB));

		// Message generation controls
		if (this.guiAccessor != null && this.guiAccessor.getSelectedContact() != null) {
			vbox.getChildren().add(new Separator());

			HBox msgBox = new HBox(8);
			vbox.getChildren().add(msgBox);

			this.msgNumber = new TextField("100");
			this.msgNumber.setPrefWidth(80);

			msgBox.getChildren().addAll(new Text("Send"), msgNumber, new Text("messages as fast as possible."));

			this.btSendMessages = new Button("Send");
			this.btSendMessages.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					ContactInterface contact = GuiMenuPlugin.this.guiAccessor.getSelectedContact();
					if (contact != null) {
						int qty = Integer.parseInt(GuiMenuPlugin.this.msgNumber.getText());

						for (int i = 1; i <= qty; i++) {
							ContentInterface content = new PlainTextContentFragment("Message " + i);
							GuiMenuPlugin.this.guiAccessor.sendMessage(contact, content);
						}
					}
				}
			});

			vbox.getChildren().add(btSendMessages);
		}

		// Test Byte controls
		if (this.guiAccessor != null && this.guiAccessor.getLoggedInJID() != null) {
			vbox.getChildren().add(new Separator());

			HBox testByteBox = new HBox(8);
			testByteBox.setAlignment(Pos.CENTER_LEFT);
			vbox.getChildren().add(testByteBox);

			// Gridpane for status and result
			GridPane gridpane = new GridPane();
			gridpane.setVisible(false);
			vbox.getChildren().add(gridpane);


			gridpane.add(new Text("Status"), 0, 0);
			gridpane.add(new Text("Result"), 0, 1);
			gridpane.add(this.byteTestStatus, 1, 0);
			gridpane.add(this.byteTestResult, 1, 1);

			this.byteQuantity = new TextField("1024");
			this.byteQuantity.setPrefWidth(100);

			testByteBox.getChildren().addAll(new Text("Send"), this.byteQuantity, new Text("Bytes in message stanza to yourself."));

			this.btSendBytes = new Button("Send");
			this.btSendBytes.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					try {
						gridpane.setVisible(true);

						// Message prefix to identify it as a test message from this plugin
						String prefix      = "###" + GuiMenuPlugin.this.pluginSet.getName() + " Test###";
						byte[] prefixBytes = prefix.getBytes("UTF-8");

						// Generate random bytes.
						int    length = Integer.parseInt(GuiMenuPlugin.this.byteQuantity.getText()) - prefixBytes.length;
						byte[] data;

						if (length > 0) {
							byte[] b = new byte[length];
//							new Random().nextBytes(b);

							data = new byte[length + prefixBytes.length];

							// Create random string with letters A-Z
							char[]        chars  = "abcdefghijklmnopqrstuvwxyz".toCharArray();
							StringBuilder sb     = new StringBuilder();
							Random        random = new Random();
							for (int i = 0; i < length; i++) {
								char c = chars[random.nextInt(chars.length)];
								sb.append(c);
							}

							System.arraycopy(prefixBytes, 0, data, 0, prefixBytes.length);
							System.arraycopy(sb.toString().getBytes(), 0, data, prefixBytes.length, length);
						} else {
							data = prefixBytes;
						}


						// Create contact to itself.
						Contact contact = new Contact(GuiMenuPlugin.this.guiAccessor.getLoggedInJID(), "", false, null);

						// Set status text
						GuiMenuPlugin.this.byteTestStatus.setText("Generating message stanza");
						GuiMenuPlugin.this.byteTestResult.setText("waiting...");

						// Send message
						ContentInterface content;
						content = new PlainTextContentFragment(new String(data, "UTF-8"));
						GuiMenuPlugin.this.guiAccessor.sendMessage(contact, content);
					} catch (UnsupportedEncodingException e) {
						return;
					}

				}
			});
			testByteBox.getChildren().add(btSendBytes);
		}

		stage.show();
	}

	@Override
	public Collection<MenuItem> getMenuItems() {
		MenuItem item = new MenuItem("Debug");
		item.setOnAction(e -> openDebugWindow());
		return Arrays.asList(item);
	}

	@Override
	public void setGuiAccessor(GuiPluginAccessorInterface guiAccessor) {
		this.guiAccessor = guiAccessor;
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
		// Do nothing
	}

	@Override
	public void registerErrorObserver(ProtocolErrorObserverInterface observer) {
		// Do nothing
	}

	@Override
	public void unregisterErrorObserver(ProtocolErrorObserverInterface observer) {
		// Do nothing
	}
}
