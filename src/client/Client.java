package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import server.ServerMessageModel.MsgType;

public class Client {

	private Socket s = null;
	private InetAddress ip;
	private int port = 1234;
	private ObjectInputStream inC;
	private ObjectOutputStream outC;
	
	public void init() throws UnknownHostException, IOException {
		ip = InetAddress.getByName("127.0.0.1");
		try {
			s = new Socket(ip, port);
		} catch(ConnectException exc) {
			Stage stageAlert = new Stage();
			
			BorderPane root = new BorderPane();
			FXMLLoader alertLoader = new FXMLLoader(getClass().getResource("alert.fxml"));
			root.setCenter(alertLoader.load());
			
			AlertController alertController = alertLoader.getController();
			alertController.init(stageAlert, "ERROR - Unable to contact the server", MsgType.ERROR);
			
			Scene scene = new Scene(root, 400, 200);
			stageAlert.setScene(scene);
			stageAlert.show(); 
			return;
		}
		
		outC = new ObjectOutputStream(s.getOutputStream());
		inC = new ObjectInputStream(s.getInputStream());
		
	}
	
	public ObjectOutputStream getOutputStream() {
		return outC;
	}
	
	public ObjectInputStream getInputStream() throws IOException {
		return inC;
	}
	
	public void sendToServer(Object dataToSend) throws IOException {
		if(dataToSend != null) {
			outC.writeObject(dataToSend);
		}
	}
	
	public Object receiveFromServer() throws IOException, ClassNotFoundException {
		return inC.readObject();
	}
	
	public void closeChannel(String name) throws IOException {
		outC.writeObject("Disconnessione del client "+ name);
		outC.close();
		inC.close();
		s.close();
	}
	
}
