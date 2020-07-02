package client;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ListController {

	@FXML
	private TextField nomeUtente;
	
	@FXML
	private ListView<Email> messageList;
	
	private DataModel model;
	
	public void initModel(DataModel model) {
		if(this.model != null) {
			throw new IllegalStateException("Model can only be initialized once!");
		}
		
		this.model = model;
		
		nomeUtente.setText("Casella di posta di Nome Cognome");
		nomeUtente.setDisable(false);
		
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
		 */
		messageList.setCellFactory(lv -> new ListCell<Email>() {
			@Override
			public void updateItem(Email email, boolean empty) {
				super.updateItem(email, empty);
				if(empty) {
					setText(null);
				} else {
					setText(email.getId() + " " +
							email.getMittente() + " " +
							email.getDestinatario() + " " +
							email.getArgomento() + " " +
							email.getTesto() + " ");
				}
			}
		});
	}
}
