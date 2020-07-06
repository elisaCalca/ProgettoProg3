package client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MailListController {

	@FXML
	private Label nomeUtente;
	
	@FXML
	private Label indMail;
	
	@FXML
	private ListView<Email> messageList;
	
	private CasellaPostaViewModel model;
	
	public void initData(String name, String surname) {
		nomeUtente.setText("Casella di posta di " + name + " " + surname);
		indMail.setText(name + "." + surname + "@mymaildomain.it");
	}
	
	public void initModel(CasellaPostaViewModel model) {
		if(this.model != null) {
			throw new IllegalStateException("Model can only be initialized once!");
		}
		
		this.model = model;

		messageList.setItems(model.getMessageList());
		messageList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			model.setCurrentEmail(newSelection);
		});
		
		model.currentEmailProperty().addListener((obs, oldEmail, newEmail) -> {
			if(newEmail == null) {
				messageList.getSelectionModel().clearSelection();
			} else {
				messageList.getSelectionModel().select(newEmail);
			}
		});
		
		/*
		 * Non ho capito cosa fa - MA - se commentato, nella listView si vedono gli indirizzi di memoria e non le mail
		 * Probabilmente è quello che concatena il contenuto complesso delle righe della lista
		 */
		messageList.setCellFactory(lv -> new ListCell<Email>() {
			@Override
			public void updateItem(Email email, boolean empty) {
				super.updateItem(email, empty);
				if(empty) {
					setText(null);
				} else {
					setText(email.getId() + " " +
							email.getDate() + " " +
							email.getMittente() + " " +
							email.getDestinatario() + " " +
							email.getArgomento() + " " +
							email.getTesto() + " ");
				}
			}
		});
	}
}
