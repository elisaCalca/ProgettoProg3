package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
//	private BufferedReader in = null;
//	private PrintWriter out = null;
	private ObjectInputStream inC;
	private ObjectOutputStream outC;
	
	public void init() throws UnknownHostException, IOException {
		System.out.println("inizio client init");
		ip = InetAddress.getByName("127.0.0.1"); 	//localhost
		s = new Socket(ip, port);
		System.out.println("metà client init");
//		//creazione stream di input da socket
//		InputStreamReader isr = new InputStreamReader(s.getInputStream());
//		in = new BufferedReader(isr);
//		
//		// creazione stream di output su socket
//		OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());
//		BufferedWriter bw = new BufferedWriter(osw);
//		out = new PrintWriter(bw, true);
		
//		inC = new ObjectInputStream(s.getInputStream());
//		outC = new ObjectOutputStream(s.getOutputStream());
		
	}
//	
	public ObjectOutputStream getOutputStream() {
		return outC;
	}
	
	public ObjectInputStream getInputStream() {
		return inC;
	}
	
	//se non viene usato eliminarlo!
	public void sendToServer(Object dataToSend) throws IOException {
		outC = new ObjectOutputStream(s.getOutputStream());
		System.out.println("dentro a send data to server");
		if(dataToSend != null) {
			outC.writeObject(dataToSend);
			System.out.println("str send to server");
		}
	}
	
	public Object receiveFromServer() throws IOException, ClassNotFoundException {
		inC = new ObjectInputStream(s.getInputStream());
		return inC.readObject();
	}
	
	public void closeChannel(String name) throws IOException {
		outC.writeObject("Disconnessione del client "+ name);
//		out.println("Disconnessione dal client "+ name);
		outC.close();
		inC.close();
		s.close();
	}
	
}
