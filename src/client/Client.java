package client;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Client extends Application{

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login.fxml"));
		root.setCenter(loginLoader.load());
		
		LogInController logInController = loginLoader.getController();
		logInController.init();
		
		Scene scene = new Scene(root, 495, 128);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
