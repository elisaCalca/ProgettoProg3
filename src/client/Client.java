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
		ip = InetAddress.getByName("127.0.0.1"); 	//localhost
		s = new Socket(ip, port);
		
		outC = new ObjectOutputStream(s.getOutputStream());
		inC = new ObjectInputStream(s.getInputStream());
		
	}
//	
	
	
	public ObjectOutputStream getOutputStream() {
		return outC;
	}
	
	public ObjectInputStream getInputStream() throws IOException {
		return inC;
	}
	
	public void sendToServer(Object dataToSend) throws IOException {
//		outC = new ObjectOutputStream(s.getOutputStream());
		if(dataToSend != null) {
			outC.writeObject(dataToSend);
//			outC.flush();
//			outC.reset();
		}
	}
	
	public Object receiveFromServer() throws IOException, ClassNotFoundException {
//		inC = new ObjectInputStream(s.getInputStream());
		return inC.readObject();
	}
	
	public void closeChannel(String name) throws IOException {
		outC.writeObject("Disconnessione del client "+ name);
		outC.close();
		inC.close();
		s.close();
	}
	
}
