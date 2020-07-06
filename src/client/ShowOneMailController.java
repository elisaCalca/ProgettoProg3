package client;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ShowOneMailController {
	
	@FXML
	private TextField dateField;
	
	@FXML
	private TextField mittenteField;
	
	@FXML
	private TextField destinatarioField;
	
	@FXML
	private TextField argomentoField;
	
	@FXML
	private TextField testoField;
	
	@FXML
	private Button buttonDelete;
	
	@FXML
	private Button buttonForward;
	
	@FXML
	private Button buttonReply;
	
	@FXML
	private Button buttonWriteNew;
	
	private CasellaPostaViewModel model;
	
	public void initModel(CasellaPostaViewModel model) {
		if(this.model != null) {
			throw new IllegalStateException("Model can only be initialized once!");
		}

		this.model = model;
		
		model.loadMessageList(); //aggiunto per far caricare le email esistenti nella casella
		
		model.currentEmailProperty().addListener((obs, oldEmail, newEmail) -> {
			if(oldEmail != null) {
				dateField.textProperty().unbindBidirectional(oldEmail.dateProperty());
				mittenteField.textProperty().unbindBidirectional(oldEmail.mittenteProperty());
				destinatarioField.textProperty().unbindBidirectional(oldEmail.destinatariProperty());
				argomentoField.textProperty().unbindBidirectional(oldEmail.argomentoProperty());
				testoField.textProperty().unbindBidirectional(oldEmail.testoProperty());
			}
			if(newEmail == null) {
				dateField.setText("");
				mittenteField.setText("");
				destinatarioField.setText("");
				argomentoField.setText("");
				testoField.setText("");
			} else {
				dateField.textProperty().bindBidirectional(newEmail.dateProperty());
				mittenteField.textProperty().bindBidirectional(newEmail.mittenteProperty());
				destinatarioField.textProperty().bindBidirectional(newEmail.destinatariProperty());
				argomentoField.textProperty().bindBidirectional(newEmail.argomentoProperty());
				testoField.textProperty().bindBidirectional(newEmail.testoProperty());
				buttonDelete.setDisable(false);
			}
		});
		
		buttonForward.setOnAction((ActionEvent e) -> {
			
		});
		
		buttonReply.setOnAction((ActionEvent e) -> {
			
		});
		
		buttonDelete.setOnAction((ActionEvent e) -> {
			if(model.getMessageList().size() > 0) {		//serve l'if altrimenti, se la lista è vuota, da eccezione
				model.getMessageList().remove(model.currentEmailProperty().get());
				model.setCurrentEmail(null);;
			}
			if(model.getMessageList().size() == 0) {
				//manca il controllo che lo riabilita se da ZERO messaggi torna ad essercene ALMENO UNO (riga 69 testare)
				buttonDelete.setDisable(true);
			}
		});
		
		buttonWriteNew.setOnAction((ActionEvent e) -> {
			
		});
		
	}

}
