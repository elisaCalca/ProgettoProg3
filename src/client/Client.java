package client;

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
		FXMLLoader listLoader = new FXMLLoader(getClass().getResource("list.fxml"));
		root.setCenter(listLoader.load());
		MailListController listController = listLoader.getController();
		
		FXMLLoader editorLoader = new FXMLLoader(getClass().getResource("showonemail.fxml"));
		root.setRight(editorLoader.load());
		ShowOneMailController editorController = editorLoader.getController();
		
		CasellaPostaViewModel casellaPosta = new CasellaPostaViewModel();
		listController.initModel(casellaPosta);
		editorController.initModel(casellaPosta);
		
		Scene scene = new Scene(root, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	
	}

}
