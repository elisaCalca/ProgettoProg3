package server;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.ServerMessageModel.MsgType;

public class ServerApplication extends Application{
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		ServerModel server = new ServerModel();	//istanzia il model del server
		
		//alla chiusura della finestra con la X termina tutti i thread
		primaryStage.setOnCloseRequest((WindowEvent e) -> {
			Platform.exit();
	        System.exit(0);
		});
		
		//serve un nuovo thread per non bloccare l'interfaccia
		new Thread (() ->{
			try {
				Server s = new Server();
				s.activate(server);
			} catch (IOException e) {
				System.err.println("IOException in ServerApplication");
				e.printStackTrace();
			}
		}).start();
		
		BorderPane root = new BorderPane();
		FXMLLoader serverLoader = new FXMLLoader(getClass().getResource("server.fxml"));
		root.setCenter(serverLoader.load());
		
		server.addServerMessage(new ServerMessageModel(MsgType.INFO, "Server was started"));
		ServerController serverController = serverLoader.getController();
		serverController.initModel(server);
		
		Scene scene = new Scene(root, 600, 500);
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	

	public static void main(String[] args) {

		launch(args);
		
	}
}
