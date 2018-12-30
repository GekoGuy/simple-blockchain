package de.akoeck.simple.blockchain.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
public class SimpleBlockchainClient extends Application {

	private ConfigurableApplicationContext context;
	private Parent root;
		
	@Override
	public void init() throws Exception {
		context = SpringApplication.run(SimpleBlockchainClient.class);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/wallet.fxml"));
		loader.setControllerFactory(context::getBean);
		root = loader.load();
	}
	
	@Override
	public void stop() throws Exception {
		context.close();
		super.stop();
	}
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(new Scene(root, 640, 327));
		primaryStage.centerOnScreen();
		primaryStage.show();
	}


	public static void main(String[] args) {
		Application.launch(args);
	}

}
