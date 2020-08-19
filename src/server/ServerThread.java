package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import client.EmailModel;
import javafx.application.Platform;
import mailutils.MailUtils;
import server.ServerMessageModel.MsgType;

public class ServerThread extends Thread {
	
	private boolean condition = true;
	private Socket s = null;
	private ObjectInputStream in;	//stream object for receiving object
	private ObjectOutputStream out;	//stream object for sending object
	private ServerModel model;	//riceve la stessa istanza del model creata all'inizio in Server.java
	private String clientName = "";
	
	public ServerThread(Socket socket, ServerModel model) throws IOException, ClassNotFoundException {
		super("ServerThread");
		s = socket;
		this.model = model;
		
		out = new ObjectOutputStream(s.getOutputStream());
	}
	
	public void run() {
		try {
			
			in = new ObjectInputStream(s.getInputStream());
			//continua ad ascoltare messaggi dal client a cui è connesso
			while(condition) {
			
				Object received = in.readObject();	//IOException quando il client cade
				if(received == null) {
					condition = false; 
					System.out.println("condition = false in serverThread");
				}
				
				if(received instanceof EmailModel) {
					//ricevuto email, CASI :va nel cestino, esce dal cestino, deve andare ai destinatari
					EmailModel receivedEmail = (EmailModel)received;
					
					//se io non sono tra i destinatari della mail, la mail la sto inviando io
					if(!receivedEmail.getDestinatari().contains(clientName)) {
						sortingCenter(receivedEmail);
						Platform.runLater(() -> {
							model.addServerMessage(new ServerMessageModel(MsgType.INFO, clientName + "sent the following email " + receivedEmail.toString()));
						});
					} else if(receivedEmail.getId() < 0) {
						//se l'id è negativo deve andare nel cestino
						//sincronizzare i blocchi sul file che stanno leggendo
						
					} else {
						//altrimenti sta uscendo dal cestino
						
					}
					
					
				} else if(received instanceof String) {
					//il client si è appena connesso e invia il proprio nome al server
					String receivedStr = (String)received;
					if(receivedStr.contains("Name ")) {
						String name = receivedStr.replaceAll("Name ", "");
						clientName = name;
						model.getLstClientAssociated().put(name, this);
						Platform.runLater(() -> {
							model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Messages request received from " + name));
						});
						//il server legge il file con le email e gliele invia
						List<EmailModel> emails = MailUtils.readEmailsFromJSON("Files/emails.json");
						List<EmailModel> trash = MailUtils.readEmailsFromJSON("Files/Trash/" + name + "_trash.json");
						ArrayList<EmailModel> emailsToSend = new ArrayList<EmailModel>();
						for(EmailModel em : emails) {
							if(em.getDestinatari().toLowerCase().contains(name.toLowerCase()) && !MailUtils.isTrashed(em, trash)) {
								emailsToSend.add(em);
							}
						}
						out.writeObject(emailsToSend);
						Platform.runLater(() -> {
							model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Email list sent to " + name));
						});
						
					} else if(receivedStr.contains("Trash ")) {
						String name = receivedStr.replaceAll("Trash ", "");
						Platform.runLater(() -> {
							model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Trash request received from " + name));
						});
						//il server legge il suo file trash e gli invia le email cestinate
						List<EmailModel> trash = MailUtils.readEmailsFromJSON("Files/Trash/" + name + "_trash.json");
						out.writeObject(trash);
						Platform.runLater(() -> {
							model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Trashed emails sent to " + name));
						});
						
					}
					
				}
				
//				received = in.readObject();
			}
			
		} catch (IOException e) {
			System.err.println("I/O Exception");
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			System.err.println("ClassNotFoundException in ServerThread");
		} finally {
			try {
				s.close();
			} catch(IOException e2) {
				System.err.println("Something went wrong...");
			}
		}
	}
	
	public ObjectOutputStream getOutputStream() {
		return out;
	}
	
	/*
	 * Metodo che si occupa dello smistamento delle email verso i client destinatari 
	 */
	private void sortingCenter(EmailModel email) throws IOException {
		String[] clients = email.getDestinatari().split(";");
		ObjectOutputStream clientOut;
		for(String cl : clients) {
			clientOut = model.getLstClientAssociated().get(cl.trim()).getOutputStream();
			clientOut.writeObject(email);
			clientOut.flush();
			clientOut.reset();
			model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Sending message to: " + cl + " with text: " + email.getTesto()));
			
		}
	}
	
}
