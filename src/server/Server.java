package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.ServerMessageModel.MsgType;

public class Server {
	
	private int port = 1234;
	private ServerSocket ss = null;
	
	public void activate(ServerModel server) throws IOException {
		try {
			//crea un server che accetta connessioni
			ss = new ServerSocket(port);
		} catch(IOException exc) {
			System.err.println("Could not listen on port:" + port);
			System.exit(1);
		}
		
		try {
			//bloccante finchè non avviene una connessione
			while(true) {
				Socket socket = ss.accept();
				Platform.runLater(() -> {
					server.addServerMessage(new ServerMessageModel(MsgType.INFO, "Connection accepted: "+ socket));
				});
				ServerThread st = new ServerThread(socket, server);
				st.start();
			}
		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
			Platform.runLater(() -> {
				server.addServerMessage(new ServerMessageModel(MsgType.ERROR, "Accept failed"));
			});
		}
		
	}

}
