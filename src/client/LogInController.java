package client;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
	
	public void init(Stage primaryStage) {
		errorMsg1.setVisible(false);
		errorMsg2.setVisible(false);
		
		buttonLogIn.setOnAction((ActionEvent e) -> {
			String address = indirizzo.getText();
			if(!MailUtils.isNullOrEmpty(address) && MailUtils.isValidAddress(address)) {
				errorMsg1.setVisible(false);
				errorMsg2.setVisible(false);
				try {
					
					generateClient(new Stage(), indirizzo.getText());
					
				} catch (Exception exc) {
					exc.printStackTrace();
				} finally {
					primaryStage.hide();
				}
			} else {
				errorMsg1.setVisible(true);
				errorMsg2.setVisible(true);
			}
		});
	}
	
	private void generateClient(Stage primaryStage, String name) throws Exception {
		
		//Stabilire la connessione con il Server - capire se messo qui va bene
//		Socket link = new Socket(InetAddress.getLocalHost(), 1234);
//		Scanner input = new Scanner(link.getInputStream());
//		PrintWriter output = new PrintWriter(link.getOutputStream(), true);
//		output.println("In attesa di dati...");
////		String inputS = input.nextLine();
//		link.close();
		
		
//		Client c = new Client();
//		c.go();
		
		//alla chiusura della finestra con la X termina tutti i thread JavaFX
		primaryStage.setOnCloseRequest((WindowEvent e) -> {
			Platform.exit();
	        System.exit(0);
		});
		
		BorderPane root = new BorderPane();
		FXMLLoader listLoader = new FXMLLoader(getClass().getResource("list.fxml"));
		root.setLeft(listLoader.load());
		MailListController listController = listLoader.getController();
		listController.initData("Mailbox of " + name);
		
		FXMLLoader editorLoader = new FXMLLoader(getClass().getResource("showonemail.fxml"));
		root.setRight(editorLoader.load());	
		ShowOneMailController editorController = editorLoader.getController();
		
		CasellaPostaModel casellaPosta = new CasellaPostaModel();

		casellaPosta.currentUserProperty().set(name);
		listController.initModel(casellaPosta);
		editorController.initModel(casellaPosta);
		
		Scene scene = new Scene(root, 755, 450);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}

}
