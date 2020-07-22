package client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	private Socket s = null;
	private InetAddress ip;
	private int port = 1234;
	
	public void go() throws UnknownHostException, IOException {
		ip = InetAddress.getByName("127.0.0.1"); 	//localhost
		s = new Socket(ip, port);
		try {
			Scanner input = new Scanner(s.getInputStream());
			OutputStream o = s.getOutputStream();
			PrintWriter output = new PrintWriter(o, true);
			String dato = input.next();	//legge un dato
			//elabora il dato
			System.out.println("Dato:" + dato);
			System.out.println("Premi enter per mandare la ricevuta");
//			new Scanner(System.in).next();
			output.println("ricevuto!");	//manda un dato
		} catch(Exception exc) {
			exc.printStackTrace();
		} finally {
			//chiude sempre la connessione ed esce
			s.close();
		}
	}
	
}
