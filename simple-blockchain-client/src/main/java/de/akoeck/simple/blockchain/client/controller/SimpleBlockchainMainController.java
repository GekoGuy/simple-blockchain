package de.akoeck.simple.blockchain.client.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.akoeck.simple.blockchain.client.SimpleBlockchainSettings;
import de.akoeck.simple.blockchain.client.domain.Wallet;
import de.akoeck.simple.blockchain.client.util.StringUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

@Component
public class SimpleBlockchainMainController {

	private final Wallet wallet;

	private final SimpleBlockchainSettings settings;

	@FXML
	private Label address;
	
	@FXML
	private TextField recipient;

	@FXML
	private TextField amount;

	@FXML
	private Button submit;

	@FXML
	private Button reset;

	@FXML
	private MenuItem menuQuit;

	@FXML
	private MenuItem menuSave;

	@FXML
	private MenuItem menuSaveAs;

	@Autowired
	public SimpleBlockchainMainController(final SimpleBlockchainSettings settings, final Wallet wallet) {
		this.settings = settings;
		this.wallet = wallet;
	}
	
	@FXML
	public void initialize() {
		address.setText(StringUtil.byteToHexString(wallet.getPublicKey().getEncoded()));
	}

	public void quit() {
		settings.save();
		Platform.exit();
	}

	/**
	 * 
	 */
	public void save() {
		Path home = Paths.get(System.getProperty("user.home"));
		try {
			wallet.exportKeys(home.toFile());
			Alert alert = new Alert(AlertType.CONFIRMATION, "Keys exportet", ButtonType.OK);
			alert.showAndWait();
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR, "Cannot export keys", ButtonType.OK);
			alert.showAndWait();
		}
	}

	/**
	 * 
	 * @param event
	 */
	public void sendMoney() {
		if (inputValid()) {
			wallet.sendMoney(StringUtil.hexStringToByte(recipient.getText()), Long.valueOf(amount.getText()));
		} else {
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setHeaderText("Input not valid");
			errorAlert.setContentText("You must provide a recipient and an positive amount to send.");
			errorAlert.showAndWait();
		}
	}

	/**
	 * Reset the input field.s
	 * 
	 * @param event
	 */
	public void reset() {
		amount.clear();
		recipient.clear();
	}

	
	private boolean inputValid() {
		boolean isValid = false;
		if (recipient.getText() != null && !recipient.getText().isEmpty() && amount.getText() != null
				&& !amount.getText().isEmpty()) {
			isValid = true;
		}

		return isValid;
	}

}
