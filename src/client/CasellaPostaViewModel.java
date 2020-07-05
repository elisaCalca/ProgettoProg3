package client;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CasellaPostaViewModel {
	
	private ObservableList<Email> messageList = FXCollections.observableArrayList(
			message -> new Observable[] {
					message.idProperty(),
					message.mittenteProperty(),
					message.destinatarioProperty(),
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
		messageList.setAll(
				new Email(1, "M1", "D1", "Arg1", "Text1"),
				new Email(2, "M2", "D2", "Arg2", "Text2"),
				new Email(3, "M3", "D3", "Arg3", "Text3"),
				new Email(4, "M4", "D4", "Arg4", "Text4"),
				new Email(5, "M5", "D5", "Arg5", "Text5"),
				new Email(6, "M6", "D6", "Arg6", "Text6"),
				new Email(7, "M7", "D7", "Arg7", "Text7")
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
