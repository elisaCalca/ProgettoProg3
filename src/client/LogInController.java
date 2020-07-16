package client;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mailutils.MailUtils;

public class LogInController {
	
	@FXML
	private TextField indirizzo;
	
	@FXML
	private Button buttonLogIn;
	
	@FXML
	private Label errorMsg1;
	
	@FXML
	private Label errorMsg2;
	
	public void init() {
		errorMsg1.setVisible(false);
		errorMsg2.setVisible(false);
		buttonLogIn.setOnAction((ActionEvent e) -> {
			String address = indirizzo.getText();
			if(!isBlankOrEmpty(address) && MailUtils.isValidAddress(address)) {
				errorMsg1.setVisible(false);
				errorMsg2.setVisible(false);
				try {
					generateClient(new Stage(), indirizzo.getText());
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			} else {
				errorMsg1.setVisible(true);
				errorMsg2.setVisible(true);
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
		
		//Stabilire la connessione con il Server - capire se messo qui va bene
		Socket link = new Socket(InetAddress.getLocalHost(), 1234);
		Scanner input = new Scanner(link.getInputStream());
		PrintWriter output = new PrintWriter(link.getOutputStream(), true);
		output.println("In attesa di dati...");
		String inputS = input.nextLine();
		link.close();
		//
		
		BorderPane root = new BorderPane();
		FXMLLoader listLoader = new FXMLLoader(getClass().getResource("list.fxml"));
		root.setLeft(listLoader.load());
		MailListController listController = listLoader.getController();
		listController.initData(name);
		
		FXMLLoader editorLoader = new FXMLLoader(getClass().getResource("showonemail.fxml"));
		root.setRight(editorLoader.load());	
		ShowOneMailController editorController = editorLoader.getController();
		
		CasellaPostaViewModel casellaPosta = new CasellaPostaViewModel();

		casellaPosta.currentUserProperty().set(indirizzo.getText());
		listController.initModel(casellaPosta);
		editorController.initModel(casellaPosta);
		
		Scene scene = new Scene(root, 755, 450);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
