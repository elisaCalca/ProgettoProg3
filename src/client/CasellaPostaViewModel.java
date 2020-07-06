package client;

import java.util.ArrayList;
import java.util.Date;

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
	
	public void loadMessageList() {
//		ArrayList<String> d1 = new ArrayList<String>();
//		d1.add("D1");
//		ArrayList<String> d2 = new ArrayList<String>();
//		d1.add("D2");
//		ArrayList<String> d3 = new ArrayList<String>();
//		d1.add("D3");
//		ArrayList<String> d4 = new ArrayList<String>();
//		d4.add("D4");
//		ArrayList<String> d5 = new ArrayList<String>();
//		d5.add("D5");
//		ArrayList<String> d6 = new ArrayList<String>();
//		d6.add("D6");
//		ArrayList<String> d7 = new ArrayList<String>();
//		d7.add("D7");
		messageList.setAll(
				new Email(1, new Date(2020-1900, 0, 1), "M1", "D1", "Arg1", "Tanti auguri di buon 2020!"),
				new Email(2, new Date(2020-1900, 0, 20), "M2", "D2", "Arg2", "Text2"),
				new Email(3, new Date(2020-1900, 1, 5), "M3", "D3", "Arg3", "Text3"),
				new Email(4, new Date(2018-1900, 11, 10), "M4", "D4", "Arg4", "Text4"),
				new Email(5, new Date(2019-1900, 10, 8), "M5", "D5", "Arg5", "Text5"),
				new Email(6, new Date(2020-1900, 5, 20), "M6", "D6", "Arg6", "Text6"),
				new Email(7, new Date(2020-1900, 3, 20), "M7", "D7", "Arg7", "Text7")
		);
		orderByIdDesc();
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
