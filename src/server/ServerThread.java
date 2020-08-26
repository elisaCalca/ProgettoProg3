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
	
	private Socket s = null;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private ServerModel model;	//riceve la stessa istanza del model creata all'inizio in Server.java
	private Object lock;
	private String clientName = "";
	private String trashPath = "";
	private String emailsPath = "Files/emails.json";
	
	public ServerThread(Socket socket, ServerModel model, Object lock) throws IOException, ClassNotFoundException {
		super("ServerThread");
		s = socket;
		this.lock = lock;
		this.model = model;
		
		out = new ObjectOutputStream(s.getOutputStream());
	}
	
	public void run() {
		try {
			
			in = new ObjectInputStream(s.getInputStream());	//mettere nel costruttore??
			
			while(true) {
			
				Object received = in.readObject();
				
				if(received instanceof EmailModel) {
					/*
					 * Il server riceve una email da un client:
					 * - deve mandarla ai destinatari (se chi la invia non è tra i destinatari)
					 * - deve metterla nel cestino (se la mail ha ID negativo)
					 * - deve toglierla dal cestino (se la mail ha ID positivo)
					 */
					EmailModel receivedEmail = (EmailModel)received;
					
					if(!receivedEmail.getDestinatari().contains(clientName)) {
						System.out.println("Un client sta cercando di inviare una email ai destinatari");
						synchronized (lock) {
							MailUtils.addToFile(emailsPath, receivedEmail);
						}
						sortingCenter(receivedEmail);
						Platform.runLater(() -> {
							model.addServerMessage(new ServerMessageModel(MsgType.INFO, clientName + "sent the following email " + receivedEmail.toString()));
						});
					} else if(receivedEmail.getId() < 0) {
							System.out.println("Un client sta mandando una email al cestino");
							synchronized (lock) {
								MailUtils.addToFile(trashPath, receivedEmail);
								System.err.println("Email added to trash");
							}
							out.writeObject(receivedEmail);
							Platform.runLater(() -> {
								model.addServerMessage(new ServerMessageModel(MsgType.INFO, clientName + " moved an email from Mailbox to the Trash"));
							});
					} else {
						System.out.println("Un client sta togliendo una email dal cestino");
						System.out.println("sono il SERVER, ho ricevuto: " + receivedEmail);
						EmailModel copyToRemove = new EmailModel(receivedEmail.getId() * -1, receivedEmail.getDate(), receivedEmail.getMittente(), receivedEmail.getDestinatari(), receivedEmail.getArgomento(), receivedEmail.getTesto());
						System.out.println("devo rimuovere: " + copyToRemove);
						synchronized (lock) {
							MailUtils.removeFromFile(trashPath, copyToRemove);
						}
						out.writeObject(receivedEmail);
						Platform.runLater(() -> {
							model.addServerMessage(new ServerMessageModel(MsgType.INFO, clientName + " moved an email from Trash to the Mailbox"));
						});
					}
					
					
				} else if(received instanceof String) {
					//il client si è appena connesso e invia il proprio nome al server
					String receivedStr = (String)received;
					
					if(receivedStr.contains("Name ")) {
						String name = receivedStr.replaceAll("Name ", "");
						clientName = name;
						trashPath = "Files/Trash/" + clientName + "_trash.json";
						model.getLstClientAssociated().put(name, this);
						Platform.runLater(() -> {
							model.addServerMessage(new ServerMessageModel(MsgType.INFO, name + " asks for the messages list in Mailbox"));
						});
						//il server legge il file con le email e gliele invia
						List<EmailModel> emails;
						List<EmailModel> trash;
						synchronized (lock) {
							emails = MailUtils.readEmailsFromJSON(emailsPath);
							trash = new ArrayList<EmailModel>();
							trash.addAll(MailUtils.readEmailsFromJSON(trashPath));
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
							EmailModel welcome = new EmailModel(name);	//costruttore dell'email welcome
							emailsToSend.add(welcome);
							synchronized (lock) {
								MailUtils.addToFile(emailsPath, welcome);
							}
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
							model.addServerMessage(new ServerMessageModel(MsgType.INFO, name + " asks for the messages list in the Trash"));
						});
						//il server legge il suo file trash e gli invia le email cestinate
						List<EmailModel> trash;
						synchronized (lock) {
							trash = MailUtils.readEmailsFromJSON(trashPath);
						}
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
			Platform.runLater(() -> {
				model.addServerMessage(new ServerMessageModel(MsgType.WARN, "Client " + clientName + " disconnected..."));
			});
		} catch (ClassNotFoundException e1) {
			System.err.println("ClassNotFoundException in ServerThread");
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
				Platform.runLater(() -> {
					model.addServerMessage(new ServerMessageModel(MsgType.ERROR, cl + " client does not exist!"));
				});
				out.writeObject("Not exist " + cl);
			} else {
				clientOut = model.getLstClientAssociated().get(cl.trim()).getOutputStream();
				clientOut.writeObject(email);
				Platform.runLater(() -> {
					model.addServerMessage(new ServerMessageModel(MsgType.INFO, "Sending message to: " + cl + " with text: " + email.getTesto()));
				});
			}
		}
	}
	
}
