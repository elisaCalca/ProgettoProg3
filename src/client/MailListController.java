package client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MailListController {

	@FXML
	private Label indUtente;
	
	@FXML
	private ListView<EmailModel> messageList;
	
	private CasellaPostaModel model;
	
	public void initData(String name) {
		indUtente.setText(name);
	}
	
	public void initModel(CasellaPostaModel model) {
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
		 * Formatta le righe della ListView
		 */
		messageList.setCellFactory(lv -> new ListCell<EmailModel>() {
			@Override
			public void updateItem(EmailModel email, boolean empty) {
				super.updateItem(email, empty);
				if(empty) {
					setText(null);
				} else {
					setText(
							email.getDate() + "     " +
							email.getMittente() + "     " +
							email.getArgomento() + "  -  " +
							email.getTesto() + " " 
					);
				}
			}
		});
	}
}
