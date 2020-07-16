package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private int port = 1234;
	private ServerSocket ss = null;
	
	public void activate() throws IOException {
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
			ServerThread st = new ServerThread(socket);
			st.start();
		}
	}

	public static void main(String[] args) {
		try {
			Server s = new Server();
			s.activate();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
