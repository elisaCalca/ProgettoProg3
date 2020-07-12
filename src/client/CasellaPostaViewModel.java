package client;

import java.io.FileReader;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mailutils.MailUtils;

public class CasellaPostaViewModel {
	
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
	private final ObjectProperty<Email> currentEmail = new SimpleObjectProperty<>(null);

	public final ObjectProperty<Email> currentEmailProperty() {
		return this.currentEmail;
	}
	
	public final Email getCurrentEmail() {
		return this.currentEmailProperty().get();
	}
	
	public final void setCurrentEmail(Email email) {
		this.currentEmailProperty().set(email);
	}
	
	/*
	 * Definizione dell'ObservableList messageList
	 */
	private ObservableList<Email> messageList = FXCollections.observableArrayList(
			message -> new Observable[] {
					message.idProperty(),
					message.dateProperty(),
					message.mittenteProperty(),
					message.destinatariProperty(),
					message.argomentoProperty(),
					message.testoProperty()
			});
			
	public ObservableList<Email> getMessageList() {
		return messageList;
	}
	
	public void addMessage(Email emailReceived) {
		messageList.add(emailReceived);
		orderByDateDesc();
	}
	
	/*
	 * Inserisce i messaggi nella messageList
	 * Ignora il messaggio se l'utente loggato non è tra i destinatari
	 */
	public void loadMessageList() {
		List<Email> emails = MailUtils.readEmailsFromJSON("Files/emails.json");
		List<Email> trash = MailUtils.readEmailsFromJSON("Files/Trash/" + getCurrentUser() + "_trash.json");
		for(Email em : emails) {
			//Elisa.Calcaterra@mymail.com
			if(em.getDestinatari().toLowerCase().contains(getCurrentUser().toLowerCase()) && !isTrashed(em, trash)) {
				messageList.add(em);
			}
		}
		orderByDateDesc();
	}
	
	private boolean isTrashed(Email toCheck, List<Email> trash) {
		for(Email em : trash) {
			if(em.equals(toCheck)) {
				return true;
			}
		}
		return false;
	}
	
	private void orderByDateDesc() {
		Email temp;
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
