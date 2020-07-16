package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

	public static void main(String[] args) {
		try {
			int port = 1234;
			
			//Un oggetto di tipo ServerSocket rappresenta un certo servizio
			ServerSocket servSock = new ServerSocket(port);
			
			//Un oggetto di tipo socket rappresenta un particolare cliente che ha richiesto quel servizio
			Socket link = servSock.accept();
			System.out.println(link);
			Scanner input = new Scanner(link.getInputStream());
			PrintWriter output = new PrintWriter(link.getOutputStream(), true);
			output.println("In attesa di dati...");
			String inputS = input.nextLine();
			link.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
