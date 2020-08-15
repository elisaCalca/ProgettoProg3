package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

import javafx.application.Platform;
import server.ServerMessageModel.MsgType;

public class ServerThread extends Thread {
	
	private Socket s = null;
	private BufferedReader in;
	private PrintWriter out;
	private ServerModel model;	//riceve la stessa istanza del model creata all'inizio in Server.java
	
	public ServerThread(Socket socket, ServerModel model) throws IOException {
		super("ServerThread");
		s = socket;
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());
		out = new PrintWriter(new BufferedWriter(osw), true);
		this.model = model;
	}
	
	public void run() {
		try {
			
			//continua ad ascoltare messaggi dal client a cui è connesso
			while(true) {
				
				String msgReceived = in.readLine();
				
				Platform.runLater(() -> {
					model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Message received " + msgReceived));
				});
				
				if(msgReceived.contains("Name ")) {
					msgReceived.replaceAll("Name ", "");
					model.getLstClientAssociated().put(msgReceived, this);
				}
				System.out.println("lst client"+model.getLstClientAssociated());
			}
			
		} catch (IOException e) {
			System.err.println("I/O Exception");
			System.exit(-1);
		} finally {
			try {
				s.close();
			} catch(IOException exc) {
				System.err.println("Something went wrong...");
				System.exit(-1);
			}
		}
	}
	
	public PrintWriter getPrintWriter() {
		return out;
	}
	
}
