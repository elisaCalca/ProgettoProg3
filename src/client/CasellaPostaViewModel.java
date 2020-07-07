package client;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CasellaPostaViewModel {
	
	private ObservableList<Email> messageList = FXCollections.observableArrayList(
			message -> new Observable[] {
					message.idProperty(),
					message.dateProperty(),
					message.mittenteProperty(),
					message.destinatariProperty(),
					message.argomentoProperty(),
					message.testoProperty()
			});
	
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
	
	public ObservableList<Email> getMessageList() {
		return messageList;
	}
	
	public void addMessage(Email emailReceived) {
		messageList.add(emailReceived);
		orderByIdDesc();
	}
	
	@SuppressWarnings("deprecation")
	public void loadMessageList() {
		JSONParser parser = new JSONParser();
		try {
			JSONArray emailList = (JSONArray) parser.parse(new FileReader("bin/client/emails.json"));;
			
			Iterator<JSONObject> iterator = emailList.iterator();
			int index = 0;
			while (iterator.hasNext()) {
				JSONObject jsonAtt = (JSONObject)emailList.get(index);
				JSONObject jsonEmail = (JSONObject)jsonAtt.get("email");
				
				int id = Integer.parseInt((String)jsonEmail.get("id"));
				System.out.println(id);
				
				String [] res = ((String)jsonEmail.get("date")).split("/");
				Date date = new Date(Integer.parseInt(res[2])-1900, Integer.parseInt(res[1])-1, Integer.parseInt(res[0]));
				System.out.println(date);
			
				String mittente = (String)jsonEmail.get("mittente");
				System.out.println(mittente);
				
				String[] destinatari = (String[])jsonEmail.get("destinatari");
				System.out.println("DESTINATARI:"+  destinatari);
				
				String argomento = (String)jsonEmail.get("argomento");
				System.out.println(argomento);
				
				String testo = (String)jsonEmail.get("testo");
				System.out.println(testo);
				
				for(String dest : destinatari) {
					Email email = new Email(id, date, mittente, dest, argomento, testo);
					//aggiungere le if del caso
					messageList.add(email); 
				}

			}
			orderByIdDesc();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		messageList.setAll(
//				new Email(1, new Date(2020-1900, 0, 1), "M1", "D1", "Arg1", "Tanti auguri di buon 2020!"),
//				new Email(2, new Date(2020-1900, 0, 20), "M2", "D2", "Arg2", "Text2"),
//				new Email(3, new Date(2020-1900, 1, 5), "M3", "D3", "Arg3", "Text3"),
//				new Email(4, new Date(2018-1900, 11, 10), "M4", "D4", "Arg4", "Text4"),
//				new Email(5, new Date(2019-1900, 10, 8), "M5", "D5", "Arg5", "Text5"),
//				new Email(6, new Date(2020-1900, 5, 20), "M6", "D6", "Arg6", "Text6"),
//				new Email(7, new Date(2020-1900, 3, 20), "M7", "D7", "Arg7", "Text7")
//		);
//		orderByIdDesc();
	}
	
	private void orderByIdDesc() {
		Email temp;
		for(int i = 0; i < messageList.size(); i++) {
			for(int j = i+1; j < messageList.size(); j++) {
				if(messageList.get(i).getId() < messageList.get(j).getId()) {
					temp = messageList.get(i);
					messageList.set(i, messageList.get(j));
					messageList.set(j, temp);
				}
			}
		}
	}
	
}
