package de.akoeck.simple.blockchain.client.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.io.Files;

import de.akoeck.simple.blockchain.client.SimpleBlockchainSettings;
import de.akoeck.simple.blockchain.client.domain.Wallet;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
public class SimpleBlockchainMainController {

	private final Wallet wallet;

	private final SimpleBlockchainSettings settings;

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

	public void quit() {
		settings.save();
		Platform.exit();
	}

	/**
	 * 
	 */
	public void save() {

	}

	/**
	 * 
	 */
	public void saveAs() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Private Key", "*.pem"),
				new ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(null);
		try {
			Files.write(wallet.getPrivateKey().getEncoded(), selectedFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param event
	 */
	public void sendMoney() {
		if (inputValid()) {
			wallet.sendMoney(recipient.getText(), Long.valueOf(amount.getText()));
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
