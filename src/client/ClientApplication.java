package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ClientApplication extends Application{

	public static void main(String[] args) {
		launch(args);
	}
	
//Elisa.Calcaterra@mymail.com
//Giuseppe.Verdi@mymail.com
//Mario.Rossi@mymail.com
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		BorderPane root = new BorderPane();
		FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login.fxml"));
		root.setCenter(loginLoader.load());
		
		LogInController logInController = loginLoader.getController();
		logInController.init(primaryStage);
		
		Scene scene = new Scene(root, 495, 200);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}

}
