package client;

import java.io.FileReader;
import java.util.Date;

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
	@SuppressWarnings("deprecation")
	public void loadMessageList() {
		JSONParser parser = new JSONParser();
		try {
			JSONArray emailList = (JSONArray) parser.parse(new FileReader("File/emails.json"));;
			
			for (int index = 0; index < emailList.size(); index++) {
				JSONObject jsonAtt = (JSONObject)emailList.get(index);
				JSONObject jsonEmail = (JSONObject)jsonAtt.get("email");
				
				int id = Integer.parseInt((String)jsonEmail.get("id"));
				
				String [] res = ((String)jsonEmail.get("date")).split("/");
				Date date = new Date(Integer.parseInt(res[2])-1900, Integer.parseInt(res[1])-1, Integer.parseInt(res[0]));
			
				String mittente = (String)jsonEmail.get("mittente");
				
				String argomento = (String)jsonEmail.get("argomento");
				
				String testo = (String)jsonEmail.get("testo");

				JSONArray jsonDestinatari = (JSONArray)jsonEmail.get("destinatari");
				StringBuilder strDest = new StringBuilder();
				for (int nDes = 0; nDes < jsonDestinatari.size(); nDes++) {
					strDest.append((String)jsonDestinatari.get(nDes));;
					strDest.append(";");
				}
				
				//aggiunge solo quelle dell'utente loggato -- Elisa.Calcaterra@mymail.com
				if(strDest.toString().toLowerCase().contains(this.getCurrentUser().toLowerCase())) {
					messageList.add(new Email(id, date, mittente, strDest.toString(), argomento, testo));
				}
			
			}
			orderByDateDesc();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
