package client;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ShowTrashController {
	
	@FXML
	private TextField dateField;

	@FXML
	private TextField mittenteField;

	@FXML
	private TextField destinatarioField;

	@FXML
	private TextField argomentoField;

	@FXML
	private TextArea testoField;

	@FXML
	private Button buttonMoveToInbox;
	
	@FXML
	private Button buttonDeletePerm;
	
	private CasellaPostaModel model;
	
	public void initModel(CasellaPostaModel model) {
		if(this.model != null) {
			throw new IllegalStateException("Model can only be initialized once!");
		}
		
		this.model = model;
		
		//la lettura è da far fare al server, questo metodo dovrà ascoltare la socket che riceve dal server
		//mandare al server la richiesta di ricevere il cestino
		try {
			model.loadMessageList();// aggiunto per far caricare le email del file nella casella di posta 
		} catch (IOException e2) {
			e2.printStackTrace();
			System.out.println("An ERROR occured while loading message list");
		}
		
		//ascolta l'email che è attualmente selezionata
		model.currentEmailProperty().addListener((obs, oldEmail, newEmail) -> {
			if (oldEmail != null) {
				dateField.textProperty().unbindBidirectional(oldEmail.dateProperty());
				mittenteField.textProperty().unbindBidirectional(oldEmail.mittenteProperty());
				destinatarioField.textProperty().unbindBidirectional(oldEmail.destinatariProperty());
				argomentoField.textProperty().unbindBidirectional(oldEmail.argomentoProperty());
				testoField.textProperty().unbindBidirectional(oldEmail.testoProperty());
			}
			if (newEmail == null) {
				dateField.setText("");
				mittenteField.setText("");
				destinatarioField.setText("");
				argomentoField.setText("");
				testoField.setText("");
				buttonMoveToInbox.setDisable(true);
				buttonDeletePerm.setDisable(true);
			} else {
				dateField.textProperty().bindBidirectional(newEmail.dateProperty());
				mittenteField.textProperty().bindBidirectional(newEmail.mittenteProperty());
				destinatarioField.textProperty().bindBidirectional(newEmail.destinatariProperty());
				argomentoField.textProperty().bindBidirectional(newEmail.argomentoProperty());
				testoField.textProperty().bindBidirectional(newEmail.testoProperty());
				buttonMoveToInbox.setDisable(false);
				buttonDeletePerm.setDisable(false);
			}
		});
		
		if(model.getCurrentEmail() == null) {
			buttonMoveToInbox.setDisable(true);
			buttonDeletePerm.setDisable(true);
		}
		
	}

}
