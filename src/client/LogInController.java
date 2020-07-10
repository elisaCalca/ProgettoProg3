package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class LogInController {
	
	@FXML
	private TextField indirizzo;
	
	@FXML
	private Button buttonLogIn;
	
	
	public void init() {
		buttonLogIn.setOnAction((ActionEvent e) -> {
			if(!isBlankOrEmpty(indirizzo.getText()))
				try {
					
					generateClient(new Stage(), indirizzo.getText());
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			else {
				
			}
		});
	}
	
	private static boolean isBlankOrEmpty(String str) {
		if(str.isEmpty() || str.equals(null)) {
			return true;
		}
		return false;
	}
	
	private void generateClient(Stage primaryStage, String name) throws Exception {
		BorderPane root = new BorderPane();
		FXMLLoader listLoader = new FXMLLoader(getClass().getResource("list.fxml"));
		root.setCenter(listLoader.load());
		MailListController listController = listLoader.getController();
		listController.initData(name);
		
		FXMLLoader editorLoader = new FXMLLoader(getClass().getResource("showonemail.fxml"));
		root.setRight(editorLoader.load());	
		ShowOneMailController editorController = editorLoader.getController();
		
		CasellaPostaViewModel casellaPosta = new CasellaPostaViewModel();

		casellaPosta.currentUserProperty().set(indirizzo.getText());
		listController.initModel(casellaPosta);
		editorController.initModel(casellaPosta);
		
		Scene scene = new Scene(root, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
