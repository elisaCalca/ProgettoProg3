package server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import client.EmailModel;
import javafx.application.Platform;
import mailutils.MailUtils;
import server.ServerMessageModel.MsgType;

public class ServerThread extends Thread {
	
	private boolean condition = true;
	private Socket s = null;
	private ObjectInputStream in;
	private ObjectOutputStream out;
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
			while(true) {
			
				Object received = in.readObject();
				
				if(received instanceof EmailModel) {
					//ricevuto email, CASI :va nel cestino, esce dal cestino, deve andare ai destinatari
					EmailModel receivedEmail = (EmailModel)received;
					
					//se io non sono tra i destinatari della mail, la mail la sto inviando io
					if(!receivedEmail.getDestinatari().contains(clientName)) {
						System.out.println("Un client sta cercando di inviare una email a dei destinatari");
						sortingCenter(receivedEmail);
						Platform.runLater(() -> {
							model.addServerMessage(new ServerMessageModel(MsgType.INFO, clientName + "sent the following email " + receivedEmail.toString()));
						});
					} else if(receivedEmail.getId() < 0) {
						System.out.println("Un client sta cancellando una email");
						//se l'id è negativo deve andare nel cestino
						//sincronizzare i blocchi sul file che stanno leggendo
						String filepath = "Files/Trash/" + clientName + "_trash.json";
						List<EmailModel> trashList = MailUtils.readEmailsFromJSON(filepath);
						trashList.add(receivedEmail);
						MailUtils.writeEmailsInJSON(filepath, trashList);
						//gliela invio di nuovo così la inserisce nel cestino
						//TODO
						
					} else {
						System.out.println("Un client sta togliendo una email dal cestino");
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
							model.addServerMessage(new ServerMessageModel(MsgType.INFO, name + " ask for the messages list in Mailbox"));
						});
						//il server legge il file con le email e gliele invia
						List<EmailModel> emails = MailUtils.readEmailsFromJSON("Files/emails.json");
						String trashPath = "Files/Trash/" + name + "_trash.json";
						List<EmailModel> trash = new ArrayList<EmailModel>();
						try {
							trash.addAll(MailUtils.readEmailsFromJSON(trashPath));
						} catch (IOException exc) {
							trashNotFound(trashPath, emails);
						}
						ArrayList<EmailModel> emailsToSend = new ArrayList<EmailModel>();
						for(EmailModel em : emails) {
							if(em.getDestinatari().toLowerCase().contains(name.toLowerCase()) && !MailUtils.isTrashed(em, trash)) {
								emailsToSend.add(em);
							}
						}
						if(emailsToSend.size() > 0) {
							out.writeObject(emailsToSend);
							Platform.runLater(() -> {
								model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Messages list sent to " + name));
							});
							emailsToSend = new ArrayList<EmailModel>();
						} else if (emailsToSend.size() == 0 && trash.size() > 0){
							//il fatto che l'utente non abbia mail ne in inbox ne nel cestino viene considerata una cancellazione del profilo
							//quindi al login successivo riceverà la mail di benvenuto senza passare da qui
							Platform.runLater(() -> {
								model.addServerMessage(new ServerMessageModel(MsgType.WARN, "There are no message for " + name + " in the Mailbox"));
							});
						} else if(emailsToSend.size() == 0 && trash.size() == 0) {
							emailsToSend.add(new EmailModel(name));	//costruttore dell'email welcome
							out.writeObject(emailsToSend);
							Platform.runLater(() -> {
								model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Welcome message sent to " + name));
							});
							emailsToSend = new ArrayList<EmailModel>();
						} else {
							Platform.runLater(() -> {
								model.addServerMessage(new ServerMessageModel(MsgType.ERROR, "Error while loading " + name + "'s files"));
							});
						}
						
					} else if(receivedStr.contains("Trash ")) {
						String name = receivedStr.replaceAll("Trash ", "");
						Platform.runLater(() -> {
							model.addServerMessage(new ServerMessageModel(MsgType.INFO, name + " ask for the messages list in the Trash"));
						});
						//il server legge il suo file trash e gli invia le email cestinate
						List<EmailModel> trash = MailUtils.readEmailsFromJSON("Files/Trash/" + name + "_trash.json");
						out.writeObject(trash);
						if(trash.size() > 0) {
							Platform.runLater(() -> {
								model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Trashed emails sent to " + name));
							});
						} else {
							Platform.runLater(() -> {
								model.addServerMessage(new ServerMessageModel(MsgType.WARN, "There are no trashed emails for " + name));
							});
						}
						trash = new ArrayList<EmailModel>();
					}
					
				}
				
			}
			
		} catch (IOException e) {
			System.err.println("I/O Exception gestita in ServerThread");
		} catch (ClassNotFoundException e1) {
			System.err.println("ClassNotFoundException in ServerThread");
		} finally {
			System.out.println("s.close() in server thread because the client is down");
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ObjectOutputStream getOutputStream() {
		return out;
	}
	
	/*
	 * Metodo che si occupa dello smistamento delle email verso i client destinatari se esistono
	 */
	private void sortingCenter(EmailModel email) throws IOException {
		String[] clients = email.getDestinatari().split(";");
		ObjectOutputStream clientOut;
		for(String cl : clients) {
			if(!model.getLstClientAssociated().containsKey(cl)) {
				model.addServerMessage(new ServerMessageModel(MsgType.ERROR, cl + " client does not exist!"));
			} else {
				clientOut = model.getLstClientAssociated().get(cl.trim()).getOutputStream();
				clientOut.writeObject(email);
				model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Sending message to: " + cl + " with text: " + email.getTesto()));
			}
		}
	}
	
	private void trashNotFound(String filepath, List<EmailModel> emails) {
		System.err.println("Gestione file not found");	//filenotfound è sottoclasse di IO
		if(filepath.contains("trash")) {
			File f = new File(filepath);
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
				
				//write an empty array in the new trash file
				MailUtils.writeEmailsInJSON(filepath, emails);
				
			} catch(IOException exc) {
				exc.printStackTrace();
				System.err.println("Error while creating trash file");
			}
		}
	}
	
}
