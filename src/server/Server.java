package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.ServerMessageModel.MsgType;

public class Server extends Application{
	
	private int port = 1234;
	private ServerSocket ss = null;
	
	public void activate(ServerModel server) throws IOException {
		try {
			//crea un server che accetta connessioni e rappresenta un certo servizio
			ss = new ServerSocket(port);
		} catch(IOException exc) {
			System.err.println("Could not listen on port:" + port);
			System.exit(1);
		}
		
		//rimane in attesa di connessioni
		while(true) {
			//Un oggetto di tipo socket rappresenta un particolare cliente che ha richiesto quel servizio
			Socket socket = ss.accept();
			ServerThread st = new ServerThread(socket, server);
			st.start();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		//alla chiusura della finestra con la X termina tutti i thread JavaFX
		primaryStage.setOnCloseRequest((WindowEvent e) -> {
			Platform.exit();
	        System.exit(0);
		});
		
		ServerModel server = new ServerModel();	//istanzia il model del server
		
		new Thread (() ->{
			try {
				Server s = new Server();
				s.activate(server);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		
		BorderPane root = new BorderPane();
		FXMLLoader serverLoader = new FXMLLoader(getClass().getResource("server.fxml"));
		root.setCenter(serverLoader.load());
		
		server.addServerMessage(new ServerMessageModel(MsgType.INFO, new Date(), "Server was started"));
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
