package client;

import java.io.IOException;
import java.util.List;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mailutils.MailUtils;

public class CasellaPostaModel {
	
	/*
	 * Definizione della proprietà Client - Readonly
	 */
	private Client c;
	
	public Client getClient() {
		return c;
	}
	
	/*
	 * Costruttore
	 */
	public CasellaPostaModel(Client c) {
		this.c = c;
	}
	
	/*
	 * Definizione della property currentUser
	 */
	private final StringProperty currentUser = new SimpleStringProperty();
	
	public final StringProperty currentUserProperty() {
		return this.currentUser;
	}
	
	public final String getCurrentUser() {
		return this.currentUserProperty().get();
	}
	
	public final void setCurrentUser(String usr) {
		this.currentUserProperty().set(usr);
	}
	
	/*
	 * Definizione della property currentEmail
	 */
	private final ObjectProperty<EmailModel> currentEmail = new SimpleObjectProperty<>(null);

	public final ObjectProperty<EmailModel> currentEmailProperty() {
		return this.currentEmail;
	}
	
	public final EmailModel getCurrentEmail() {
		return this.currentEmailProperty().get();
	}
	
	public final void setCurrentEmail(EmailModel email) {
		this.currentEmailProperty().set(email);
	}
	
	/*
	 * Definizione dell'ObservableList messageList
	 */
	private ObservableList<EmailModel> messageList = FXCollections.observableArrayList(
			message -> new Observable[] {
					message.idProperty(),
					message.dateProperty(),
					message.mittenteProperty(),
					message.destinatariProperty(),
					message.argomentoProperty(),
					message.testoProperty()
			});
			
	public ObservableList<EmailModel> getMessageList() {
		return messageList;
	}
	
	public void addMessage(EmailModel emailReceived) {
		messageList.add(emailReceived);
		orderByDateDesc();
	}
	
	/*
	 * Inserisce i messaggi nella messageList
	 * Ignora il messaggio se l'utente loggato non è tra i destinatari
	 */
	public void loadMessageList() throws IOException, ClassNotFoundException {
//		//invece che leggerli direttamente dal file dovrà farseli spedire dalla socket
//		List<EmailModel> emails = MailUtils.readEmailsFromJSON("Files/emails.json");
//		List<EmailModel> trash = MailUtils.readEmailsFromJSON("Files/Trash/" + getCurrentUser() + "_trash.json");
//		for(EmailModel em : emails) {
//			if(em.getDestinatari().toLowerCase().contains(getCurrentUser().toLowerCase()) && !trash.contains(em)) {
//				messageList.add(em);
//			}
//		}
		
//		new Thread(() ->  {
			Object received;
			try {
				received = c.receiveFromServer();
				while(received != null) {
					messageList.add((EmailModel)received);
				}
			} catch (ClassNotFoundException | IOException e) {
				System.out.println("Error while loading messageList");
				e.printStackTrace();
			}
//		});
		
		orderByDateDesc();
		System.out.println("ho caricato la message list!!!");
	}
	
	public void loadTrashMessageList() throws IOException, ClassNotFoundException {
		//invece che leggerli direttamente dal file dovrà farseli spedire dalla socket
//		List<EmailModel> trash = MailUtils.readEmailsFromJSON("Files/Trash/" + getCurrentUser() + "_trash.json");
//		for(EmailModel em : trash) {
//			messageList.add(em);
//			
//		}
		Object received = c.receiveFromServer();
		while(received != null) {
			messageList.add((EmailModel)received); 
		}
		System.out.println("received trash message list");
		orderByDateDesc();
	}
	
	
	private void orderByDateDesc() {
		EmailModel temp;
		for(int i = 0; i < messageList.size(); i++) {
			for(int j = i+1; j < messageList.size(); j++) {
				if((messageList.get(i).getDate()).compareTo(messageList.get(j).getDate()) < 0) {
					temp = messageList.get(i);
					messageList.set(i, messageList.get(j));
					messageList.set(j, temp);
				}
			}
		}
	}
	
}
