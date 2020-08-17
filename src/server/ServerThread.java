package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import client.EmailModel;
import javafx.application.Platform;
import mailutils.MailUtils;
import server.ServerMessageModel.MsgType;

public class ServerThread extends Thread {
	
	private Socket s = null;
	private ObjectInputStream in;	//stream object for receiving object
	private ObjectOutputStream out;	//stream object for sending object
	private ServerModel model;	//riceve la stessa istanza del model creata all'inizio in Server.java
	
	public ServerThread(Socket socket, ServerModel model) throws IOException, ClassNotFoundException {
		super("ServerThread");
		s = socket;
		this.model = model;
//		
//		in = new ObjectInputStream(s.getInputStream());
		out = new ObjectOutputStream(s.getOutputStream());
		System.out.println("fine costruttore server thread");
	}
	
	public void run() {
		try {
			
			//continua ad ascoltare messaggi dal client a cui è connesso
//			while(true) {
			do {
				in = new ObjectInputStream(s.getInputStream());
				System.out.println("While true");
				Object received = in.readObject();
				
				if(received == null)	break; 
//			while (received != null) {
				
				if(received instanceof EmailModel) {
					EmailModel receivedEmail = (EmailModel)received;
					
					sortingCenter(receivedEmail);
					
					Platform.runLater(() -> {
						model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Email received " + receivedEmail.toString()));
					});
				} else if(received instanceof String) {
					//il client si è appena connesso e invia il proprio nome al server
					String receivedStr = (String)received;
					if(receivedStr.contains("Name ")) {
						String name = receivedStr.replaceAll("Name ", "");
						model.getLstClientAssociated().put(name, this);
						Platform.runLater(() -> {
							model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Messages request received from " + name));
						});
						//il server legge il suo file con le email e gliele invia
						List<EmailModel> emails = MailUtils.readEmailsFromJSON("Files/emails.json");
						List<EmailModel> trash = MailUtils.readEmailsFromJSON("Files/Trash/" + name + "_trash.json");
						for(EmailModel em : emails) {
							if(em.getDestinatari().toLowerCase().contains(name.toLowerCase()) && !trash.contains(em)) {
								out.writeObject(em);
								Platform.runLater(() -> {
									model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Email send to " + name));
								});
							}
						}
						
					} else if(receivedStr.contains("Trash ")) {
						String name = receivedStr.replaceAll("Trash ", "");
						Platform.runLater(() -> {
							model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Trash request received from " + name));
						});
						//il server legge il suo file trash e gli invia le email cestinate
						List<EmailModel> trash = MailUtils.readEmailsFromJSON("Files/Trash/" + name + "_trash.json");
						for(EmailModel em : trash) {
							out.writeObject(em);;
							Platform.runLater(() -> {
								model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Trashed email send to " + name));
							});
						}
						
					}
					
				}
				
//				received = in.readObject();
			}while (in != null);
			//quando esce dal while non sta più ascoltando
//			s.close();
			
		} catch (IOException e) {
			System.err.println("I/O Exception");
			e.printStackTrace();
//			System.exit(-1);
		} catch (ClassNotFoundException e1) {
			System.err.println("ClassNotFoundException during cast");
//			System.exit(-1);
		} finally {
			try {
				s.close();
			} catch(IOException e2) {
				System.err.println("Something went wrong...");
//				System.exit(-1);
			}
		}
	}
	
	public ObjectOutputStream getOutputStream() {
		return out;
	}
	
	/*
	 * Metodo che si occupa dello smistamento delle mail verso i client destinatari 
	 */
	private void sortingCenter(EmailModel email) throws IOException {
		String[] clients = email.getDestinatari().split(";");
		ObjectOutputStream clientOut;
		for(String cl : clients) {
			clientOut = model.getLstClientAssociated().get(cl.trim()).getOutputStream();
			clientOut.writeObject(email);
			model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Sending message to: " + cl + " with text: " + email.getTesto()));
			
		}
	}
	
}
