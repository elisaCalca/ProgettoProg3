package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerThread extends Thread {
	
	private Socket s = null;
	private ServerModel model;
	
	public ServerThread(Socket socket, ServerModel model) {
		super("ServerThread");
		s = socket;
		this.model = model;
	}
	
	public void run() {
		try {
			OutputStream o = s.getOutputStream();
			PrintWriter output = new PrintWriter(o, true);
			InputStream i = s.getInputStream();
			Scanner input = new Scanner(i);
			output.println("Ciao caro client!");	//spedisce un dato
			String dato = input.next();		//legge un dato
			System.out.println("Dato: " + dato);	//elabora il dato
			model.addServerMessage(new ServerMessageModel());
			
		} catch (IOException e) {
			System.err.println("I/O Exception");
			System.exit(1);
		} finally {
			try {
				s.close();
			} catch(IOException exc) {
				System.err.println("Something went wrong...");
				System.exit(0);
			}
		}
		
	}
	
}
