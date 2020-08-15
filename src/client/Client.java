package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	private Socket s = null;
	private InetAddress ip;
	private int port = 1234;
	private BufferedReader in = null;
	private PrintWriter out = null;
	
	public void init() throws UnknownHostException, IOException {
		ip = InetAddress.getByName("127.0.0.1"); 	//localhost
		s = new Socket(ip, port);
		
		//creazione stream di input da socket
		InputStreamReader isr = new InputStreamReader(s.getInputStream());
		in = new BufferedReader(isr);
		
		// creazione stream di output su socket
		OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());
		BufferedWriter bw = new BufferedWriter(osw);
		out = new PrintWriter(bw, true);
	}
	
	public boolean sendToServer(String dataToSend) {
		if(dataToSend != null) {
			out.println(dataToSend);
			return true;
		}
		return false;
	}
	
	public void closeChannel(String name) throws IOException {
		out.println("Disconnessione del client "+ name);
		out.close();
		in.close();
		s.close();
	}
	
}
